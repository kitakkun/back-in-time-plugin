package com.github.kitakkun.backintime.compiler.backend

import com.github.kitakkun.backintime.compiler.BackInTimeCompilerConfiguration
import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import com.github.kitakkun.backintime.compiler.backend.analyzer.UserDefinedValueContainerAnalyzer
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.isReifiable
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.isVararg

class BackInTimePluginContext(
    baseContext: IrPluginContext,
    config: BackInTimeCompilerConfiguration,
    moduleFragment: IrModuleFragment,
) : IrPluginContext by baseContext {
    val pluginContext: IrPluginContext = baseContext
    val valueContainerClassInfoList: List<ValueContainerClassInfo> = config.valueContainers + UserDefinedValueContainerAnalyzer.analyzeAdditionalValueContainerClassInfo(moduleFragment)

    // BackInTimeService
    val backInTimeServiceClassSymbol = referenceClass(BackInTimeConsts.backInTimeDebugServiceClassId)!!
    val notifyValueChangeFunctionSymbol = backInTimeServiceClassSymbol.getSimpleFunction(BackInTimeConsts.notifyPropertyChanged)!!
    val backInTimeNotifyMethodCallFunction = backInTimeServiceClassSymbol.getSimpleFunction(BackInTimeConsts.notifyMethodCallFunctionName) ?: error("notifyMethodCall is not found")
    val registerFunction = backInTimeServiceClassSymbol.getSimpleFunction(BackInTimeConsts.registerFunctionName)!!

    val backInTimeDebuggableInterfaceType = referenceClass(BackInTimeConsts.backInTimeDebuggableInterfaceClassId)!!.defaultType

    /**
     * Used in BackInTimeCallRegisterOnInitTransformer
     */
    // find by isPrimary because kotlinx-serialization generates secondary constructor
    val instanceInfoConstructor = referenceConstructors(BackInTimeConsts.instanceInfoClassId).first { it.owner.isPrimary }
    val propertyInfoClass = referenceClass(BackInTimeConsts.propertyInfoClassId)!!
    val propertyInfoClassConstructor = propertyInfoClass.constructors.first { it.owner.isPrimary }
    val listOfFunction = referenceFunctions(BackInTimeConsts.listOfFunctionId).first { it.owner.valueParameters.size == 1 && it.owner.valueParameters.first().isVararg }


    private val backInTimeRuntimeExceptionClassSymbol = referenceClass(BackInTimeConsts.backInTimeRuntimeExceptionClassId)!!
    val nullValueNotAssignableExceptionConstructor = backInTimeRuntimeExceptionClassSymbol.owner.sealedSubclasses
        .first { it.owner.classId == BackInTimeConsts.nullValueNotAssignableExceptionClassId }.constructors.first()
    val noSuchPropertyExceptionConstructor = backInTimeRuntimeExceptionClassSymbol.owner.sealedSubclasses
        .first { it.owner.classId == BackInTimeConsts.noSuchPropertyExceptionClassId }.constructors.first()

    // kotlinx-serialization
    val backInTimeJsonGetter = referenceProperties(BackInTimeConsts.backInTimeJsonCallableId).single().owner.getter!!
    val encodeToStringFunction = referenceFunctions(BackInTimeConsts.kotlinxSerializationEncodeToStringCallableId).firstOrNull {
        it.owner.isReifiable() && it.owner.typeParameters.size == 1 && it.owner.valueParameters.size == 1
    } ?: error("${BackInTimeConsts.kotlinxSerializationEncodeToStringCallableId} is not found. Make sure you have kotlinx-serialization runtime dependency.")
    val decodeFromStringFunction = referenceFunctions(BackInTimeConsts.kotlinxSerializationDecodeFromStringCallableId).firstOrNull {
        it.owner.isReifiable() && it.owner.typeParameters.size == 1 && it.owner.valueParameters.size == 1
    } ?: error("${BackInTimeConsts.kotlinxSerializationDecodeFromStringCallableId} is not found. Make sure you have kotlinx-serialization runtime dependency.")
}
