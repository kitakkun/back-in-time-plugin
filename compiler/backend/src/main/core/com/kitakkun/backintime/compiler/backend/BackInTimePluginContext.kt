package com.kitakkun.backintime.compiler.backend

import com.kitakkun.backintime.compiler.backend.analyzer.UserDefinedValueContainerAnalyzer
import com.kitakkun.backintime.compiler.backend.api.VersionSpecificAPI
import com.kitakkun.backintime.compiler.backend.valuecontainer.ResolvedValueContainer
import com.kitakkun.backintime.compiler.common.BackInTimeConsts
import com.kitakkun.backintime.compiler.yaml.BackInTimeYamlConfiguration
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.javac.resolve.classId
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class BackInTimePluginContext(
    baseContext: IrPluginContext,
    moduleFragment: IrModuleFragment,
    val yamlConfiguration: BackInTimeYamlConfiguration,
) : IrPluginContext by baseContext {
    val pluginContext: IrPluginContext = baseContext
    val valueContainerClassInfoList: List<ResolvedValueContainer> = yamlConfiguration.trackableStateHolders.mapNotNull { trackableStateHolder ->
        ResolvedValueContainer.create(this, trackableStateHolder)
    } + UserDefinedValueContainerAnalyzer.analyzeAdditionalValueContainerClassInfo(this, moduleFragment)

    val backInTimeEntryPointRegisterFunctionSymbol by lazy {
        backintimeNamedFunction(
            subpackage = "core.runtime.internal",
            name = "registerBackInTimeEntryPoint",
        )
    }

    // event report functions
    val reportInstanceRegistrationFunctionSymbol by lazy { backintimeNamedFunction(name = "reportInstanceRegistration", subpackage = "core.runtime.internal") }
    val reportMethodInvocationFunctionSymbol by lazy { backintimeNamedFunction(name = "reportMethodInvocation", subpackage = "core.runtime.internal") }
    val reportPropertyValueChangeFunctionSymbol by lazy { backintimeNamedFunction(name = "reportPropertyValueChange", subpackage = "core.runtime.internal") }
    val reportNewRelationshipFunctionSymbol by lazy { backintimeNamedFunction(name = "reportNewRelationship", subpackage = "core.runtime.internal") }

    // error generation functions
    val throwNoSuchPropertyExceptionFunctionSymbol by lazy { backintimeNamedFunction(name = "throwNoSuchPropertyException", subpackage = "core.runtime.internal") }

    // capture utils
    val captureThenReturnValueFunctionSymbol by lazy { backintimeNamedFunction(name = "captureThenReturnValue", subpackage = "core.runtime.internal") }

    val listOfFunction by lazy { namedFunction("kotlin.collections", "listOf") { it.owner.valueParameters.size == 1 && it.owner.valueParameters.first().isVararg } }

    // kotlinx-serialization
    val backInTimeJsonGetter = referenceProperties(BackInTimeConsts.backInTimeJsonCallableId).single().owner.getter!!
    val decodeFromStringFunction by lazy {
        namedFunction("kotlinx.serialization", "decodeFromString") {
            VersionSpecificAPI.INSTANCE.isReifiable(it.owner) && it.owner.typeParameters.size == 1 && it.owner.valueParameters.size == 1
        }
    }

    // uuid
    val uuidFunctionSymbol by lazy { backintimeNamedFunction(name = "uuid", subpackage = "core.runtime.internal") }

    val mutableMapOfFunction by lazy { namedFunction("kotlin.collections", "mutableMapOf") }

    private fun namedFunction(
        packageName: String,
        name: String,
        filter: (IrSimpleFunctionSymbol) -> Boolean = { true },
    ): IrSimpleFunctionSymbol {
        val callableId = CallableId(FqName(packageName), Name.identifier(name))
        return pluginContext.referenceFunctions(callableId).first(filter)
    }

    private fun backintimeNamedFunction(name: String, subpackage: String? = null): IrSimpleFunctionSymbol {
        val suffix = subpackage?.let { ".$subpackage" } ?: ""
        return namedFunction("com.kitakkun.backintime$suffix", name)
    }

    private fun backintimeIrClassSymbol(name: String, subpackage: String? = null): IrClassSymbol {
        val suffix = subpackage?.let { ".$subpackage" } ?: ""
        return getIrClassSymbol("com.kitakkun.backintime$suffix", name)
    }

    private fun getIrClassSymbol(packageName: String, name: String): IrClassSymbol = pluginContext.referenceClass(classId(packageName, name))
        ?: error("Unable to find symbol. Package: $packageName, Name: $name")
}
