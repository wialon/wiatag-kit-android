package com.gurtam.wiatag_kit.example

class ChatItem(var type: MessageType, var message: String)

enum class MessageType(val value: Int) {
    INPUT(0),
    OUTPUT(1),
    COMMAND(2);
}