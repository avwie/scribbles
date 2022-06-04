package common.messagebus

interface MessageBus<T> : ReceiveBus<T>, SendBus<T>