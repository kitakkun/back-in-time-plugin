package com.github.kitakkun.backintime.debugger.feature.instance.usecase

import com.github.kitakkun.backintime.debugger.data.repository.ClassInfoRepository
import com.github.kitakkun.backintime.debugger.database.ClassInfo
import com.github.kitakkun.backintime.debugger.database.Instance
import com.github.kitakkun.backintime.debugger.database.ValueChangeInfo
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.component.InstanceBindModel
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.component.PropertyBindModel
import org.koin.core.annotation.Factory

@Factory
class ConvertToInstanceBindModelUseCase(
    private val classInfoRepository: ClassInfoRepository,
) {
    suspend operator fun invoke(instance: Instance, valueChangeInfoList: List<ValueChangeInfo>): InstanceBindModel? {
        val classInfo = classInfoRepository.select(sessionId = instance.sessionId, className = instance.className) ?: return null

        // resolve super class properties
        val superClassInfoList = mutableListOf<ClassInfo>()
        var currentClassInfo = classInfo
        while (currentClassInfo.superClassName != null) {
            val superClassInfo = classInfoRepository.select(
                sessionId = instance.sessionId,
                className = currentClassInfo.superClassName!!,
            ) ?: break
            superClassInfoList.add(superClassInfo)
            currentClassInfo = superClassInfo
        }

        val properties = superClassInfoList.map { superClassInfo ->
            superClassInfo.properties.map { info ->
                PropertyBindModel.Super(
                    name = info.name,
                    type = info.propertyType,
                    parentClassName = superClassInfo.className,
                    eventCount = valueChangeInfoList.count {
                        it.instanceId == instance.id && it.className == superClassInfo.className && it.propertyName == info.name
                    },
                )
            }
        }.flatten() + classInfo.properties.map { info ->
            when {
                info.isDebuggableStateHolder -> {
                    PropertyBindModel.DebuggableStateHolder(
                        name = info.name,
                        type = info.propertyType,
                    )
                }

                else -> {
                    PropertyBindModel.Normal(
                        name = info.name,
                        type = info.propertyType,
                        eventCount = valueChangeInfoList.count {
                            it.instanceId == instance.id && it.className == classInfo.className && it.propertyName == info.name
                        },
                    )
                }
            }
        }

        return InstanceBindModel(
            uuid = instance.id,
            className = instance.className,
            properties = properties,
            propertiesExpanded = false,
        )
    }
}
