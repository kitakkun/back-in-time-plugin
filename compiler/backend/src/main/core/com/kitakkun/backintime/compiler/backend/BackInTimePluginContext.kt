package com.kitakkun.backintime.compiler.backend

import com.kitakkun.backintime.compiler.backend.analyzer.UserDefinedValueContainerAnalyzer
import com.kitakkun.backintime.compiler.backend.valuecontainer.ValueContainerBuiltIns
import com.kitakkun.backintime.compiler.backend.valuecontainer.resolved.ResolvedValueContainer
import com.kitakkun.backintime.compiler.common.BackInTimeCompilerConfiguration
import com.kitakkun.backintime.compiler.common.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.isReifiable
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.constructors
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
    val valueContainerClassInfoList: List<ResolvedValueContainer> = ValueContainerBuiltIns.mapNotNull {
        val resolvedValueContainer = ResolvedValueContainer.create(it)
        if (resolvedValueContainer == null) {
            MessageCollectorHolder.reportWarning("Could not resolve value container: ${it.classId}")
        }
        resolvedValueContainer
    } + UserDefinedValueContainerAnalyzer.analyzeAdditionalValueContainerClassInfo(moduleFragment)

    private val internalCompilerApiPackageFqName = FqName("com.kitakkun.backintime.core.runtime.internal")

    // event report functions
    val reportInstanceRegistrationFunctionSymbol = referenceFunctions(CallableId(internalCompilerApiPackageFqName, Name.identifier("reportInstanceRegistration"))).first()
    val reportMethodInvocationFunctionSymbol = referenceFunctions(CallableId(internalCompilerApiPackageFqName, Name.identifier("reportMethodInvocation"))).first()
    val reportPropertyValueChangeFunctionSymbol = referenceFunctions(CallableId(internalCompilerApiPackageFqName, Name.identifier("reportPropertyValueChange"))).first()
    val reportNewRelationshipFunctionSymbol = referenceFunctions(CallableId(internalCompilerApiPackageFqName, Name.identifier("reportNewRelationship"))).first()

    // error generation functions
    val throwTypeMismatchExceptionFunctionSymbol = referenceFunctions(CallableId(internalCompilerApiPackageFqName, Name.identifier("throwTypeMismatchException"))).first()
    val throwNoSuchPropertyExceptionFunctionSymbol = referenceFunctions(CallableId(internalCompilerApiPackageFqName, Name.identifier("throwNoSuchPropertyException"))).first()

    // capture utils
    val captureThenReturnValueFunctionSymbol = referenceFunctions(CallableId(internalCompilerApiPackageFqName, Name.identifier("captureThenReturnValue"))).first()

    /**
     * Used in [com.kitakkun.backintime.compiler.backend.transformer.BackInTimeDebuggableConstructorTransformer]
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
    val uuidFunctionSymbol = referenceFunctions(CallableId(internalCompilerApiPackageFqName, Name.identifier("uuid"))).single()

    val mutableMapOfFunction = referenceFunctions(CallableId(FqName("kotlin.collections"), Name.identifier("mutableMapOf"))).first { it.owner.isInline }
}
