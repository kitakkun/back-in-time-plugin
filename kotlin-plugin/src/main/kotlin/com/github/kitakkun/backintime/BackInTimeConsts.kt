package com.github.kitakkun.backintime

import org.jetbrains.kotlin.javac.resolve.classId
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object BackInTimeConsts {
    val debuggableStateHolderManipulatorFqName = FqName("com.github.kitakkun.backintime.runtime.DebuggableStateHolderManipulator")

    val forceSetPropertyValueForBackInDebugMethodName = Name.identifier("forceSetPropertyValueForBackInTimeDebug")
    val firstParameterNameForGeneratedMethod = Name.identifier("propertyName")
    val secondParameterNameForGeneratedMethod = Name.identifier("value")

    val mutableLiveDataClassId = classId("androidx.lifecycle", "MutableLiveData")
    val mutableStateFlowClassId = classId("kotlinx.coroutines.flow", "MutableStateFlow")
    val mutableStateClassId = classId("androidx.compose.runtime", "MutableState")
    val mutableLiveDataFqName = mutableLiveDataClassId.asSingleFqName()
    val mutableStateFlowFqName = mutableStateFlowClassId.asSingleFqName()
    val mutableStateFqName = mutableStateClassId.asSingleFqName()

    val mutableLiveDataPostValueCallableId = CallableId(mutableLiveDataClassId, Name.identifier("postValue"))
    val mutableStateFlowValuePropertyCallableId = CallableId(mutableStateFlowClassId, Name.identifier("value"))
    val mutableStateValuePropertyCallableId = CallableId(mutableStateClassId, Name.identifier("value"))

    val backInTimeDebugServiceClassId = classId("com.github.kitakkun.backintime.runtime", "BackInTimeDebugService")
    val notifyPropertyChanged = "notifyPropertyChanged"
    val registerFunctionName = "register"
}
