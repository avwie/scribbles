package nl.avwie.vdom

typealias Update<Model, Message> = (model: Model, message: Message) -> Model
typealias Render<Model, Message> = (model: Model) -> Node<Message>

class MVU<Model, Message>(
    target: Renderer.Target<*>,
    initialState: Model,
    private val render: Render<Model, Message>,
    private val update: Update<Model, Message>,
): Dispatcher<Message> {

    var state = initialState
        private set

    private val renderer = Renderer(target, this)

    init {
        renderer.render(render(state))
    }

    override fun dispatch(message: Message) {
        state = update(state, message)
        renderer.render(render(state))
    }
}