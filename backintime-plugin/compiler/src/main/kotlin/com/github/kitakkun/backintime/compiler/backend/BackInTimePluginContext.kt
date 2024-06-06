package com.github.kitakkun.backintime.compiler.backend

import com.github.kitakkun.backintime.compiler.backend.analyzer.UserDefinedValueContainerAnalyzer
import com.github.kitakkun.backintime.compiler.configuration.BackInTimeCompilerConfiguration
import com.github.kitakkun.backintime.compiler.consts.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.isReifiable
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class BackInTimePluginContext(
    baseContext: IrPluginContext,
    config: BackInTimeCompilerConfiguration,
    moduleFragment: IrModuleFragment,
) : IrPluginContext by baseContext {
    val pluginContext: IrPluginContext = baseContext
    val valueContainerClassInfoList: List<ValueContainerClassInfo> = config.valueContainers + UserDefinedValueContainerAnalyzer.analyzeAdditionalValueContainerClassInfo(moduleFragment)

    private val internalCompilerApiPackageFqName = FqName("com.github.kitakkun.backintime.runtime.internal")

    // event report functions
    val reportInstanceRegistrationFunctionSymbol = referenceFunctions(CallableId(internalCompilerApiPackageFqName, Name.identifier("reportInstanceRegistration"))).first()
    val reportMethodInvocationFunctionSymbol = referenceFunctions(CallableId(internalCompilerApiPackageFqName, Name.identifier("reportMethodInvocation"))).first()
    val reportPropertyValueChangeFunctionSymbol = referenceFunctions(CallableId(internalCompilerApiPackageFqName, Name.identifier("reportPropertyValueChange"))).first()
    val reportNewRelationshipFunctionSymbol = referenceFunctions(CallableId(internalCompilerApiPackageFqName, Name.identifier("reportNewRelationship"))).first()

    // error generation functions
    val throwTypeMismatchExceptionFunctionSymbol = referenceFunctions(CallableId(internalCompilerApiPackageFqName, Name.identifier("throwTypeMismatchException"))).first()
    val throwNoSuchPropertyExceptionFunctionSymbol = referenceFunctions(CallableId(internalCompilerApiPackageFqName, Name.identifier("throwNoSuchPropertyException"))).first()

    val backInTimeDebuggableInterfaceType = referenceClass(BackInTimeConsts.backInTimeDebuggableInterfaceClassId)!!.defaultType

    /**
     * Used in [com.github.kitakkun.backintime.compiler.backend.transformer.ConstructorTransformer]
     */
    val propertyInfoClass = referenceClass(BackInTimeConsts.propertyInfoClassId)!!
    val propertyInfoClassConstructor = propertyInfoClass.constructors.first { it.owner.isPrimary }
    val listOfFunction = referenceFunctions(BackInTimeConsts.listOfFunctionId).first { it.owner.valueParameters.size == 1 && it.owner.valueParameters.first().isVararg }

    // kotlinx-serialization
    val backInTimeJsonGetter = referenceProperties(BackInTimeConsts.backInTimeJsonCallableId).single().owner.getter!!
    val encodeToStringFunction = referenceFunctions(BackInTimeConsts.kotlinxSerializationEncodeToStringCallableId).firstOrNull {
        it.owner.isReifiable() && it.owner.typeParameters.size == 1 && it.owner.valueParameters.size == 1
    } ?: error("${BackInTimeConsts.kotlinxSerializationEncodeToStringCallableId} is not found. Make sure you have kotlinx-serialization runtime dependency.")
    val decodeFromStringFunction = referenceFunctions(BackInTimeConsts.kotlinxSerializationDecodeFromStringCallableId).firstOrNull {
        it.owner.isReifiable() && it.owner.typeParameters.size == 1 && it.owner.valueParameters.size == 1
    } ?: error("${BackInTimeConsts.kotlinxSerializationDecodeFromStringCallableId} is not found. Make sure you have kotlinx-serialization runtime dependency.")

    // uuid
    private val uuidClass = referenceClass(BackInTimeConsts.UUIDClassId)!!
    val randomUUIDFunction = uuidClass.getSimpleFunction(BackInTimeConsts.RANDOM_UUID_FUNCTION_NAME)!!
    val toStringFunction = uuidClass.getSimpleFunction("toString")!!

    val mutableMapOfFunction = referenceFunctions(CallableId(FqName("kotlin.collections"), Name.identifier("mutableMapOf"))).first { it.owner.isInline }
}
