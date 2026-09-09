package com.kitakkun.backintime.tooling.model

data class Instance(
    val id: String,
    val className: String,
    val superClassName: String,
    val properties: List<Property>,
    val events: List<EventEntity>,
) {
    val totalEvents: Int get() = events.size
}
