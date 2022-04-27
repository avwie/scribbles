package nl.avwie.vdom

typealias Update<Model, Message, Context> = (model: Model, message: Message, ctx: Context) -> Model
typealias Render<Model, Message> = (model: Model) -> Node<Message>

open class MVU<Model, Message, Context>(
    target: Renderer.Target<*>,
    initialState: Model,
    private val context: Context,
    private val render: Render<Model, Message>,
    private val update: Update<Model, Message, Context>,
): Dispatcher<Message> {

    var state = initialState
        private set

    private val renderer = Renderer(target, this)

    init {
        renderer.render(render(state))
    }

    override fun dispatch(message: Message) {
        state = update(state, message, context)
        renderer.render(render(state))
    }
}