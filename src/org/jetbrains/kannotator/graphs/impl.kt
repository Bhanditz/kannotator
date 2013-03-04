package org.jetbrains.kannotator.graphs

import java.util.ArrayList
import java.util.HashMap

open class GraphImpl<out T, out L>(private createNodeMap: Boolean): Graph<T, L> {
    private val _nodes: MutableCollection<Node<T, L>> = ArrayList()
    private val nodeMap: MutableMap<T, Node<T, L>>? = if (createNodeMap) HashMap<T, Node<T, L>>() else null

    override val nodes: Collection<Node<T, L>> = _nodes

    override fun findNode(data: T): Node<T, L>? = nodeMap?.get(data)

    fun addNode(node: Node<T, L>) {
        _nodes.add(node)
        nodeMap?.put(node.data, node)
    }
}

abstract class NodeImpl<out T, out L> : Node<T, L> {
    private val _incomingEdges: MutableCollection<Edge<T, L>> = ArrayList()
    private val _outgoingEdges: MutableCollection<Edge<T, L>> = ArrayList()

    override val incomingEdges: Collection<Edge<T, L>> = _incomingEdges
    override val outgoingEdges: Collection<Edge<T, L>> = _outgoingEdges

    fun addIncomingEdge(edge: Edge<T, L>) {
        _incomingEdges.add(edge)
    }

    fun addOutgoingEdge(edge: Edge<T, L>) {
        _outgoingEdges.add(edge)
    }
}

open class EdgeImpl<T, L>(
        public override val label: L,
        public override val from: Node<T, L>,
        public override val to: Node<T, L>
) : Edge<T, L>

abstract class GraphBuilderImpl<TI, TO, L>(val createNodeMap: Boolean, cacheNodes: Boolean) : GraphBuilder<TI, TO, L, NodeImpl<TO, L>, EdgeImpl<TO, L>> {
    val nodeCache = if (cacheNodes) HashMap<TI, NodeImpl<TO, L>>() else null

    val graph: GraphImpl<TO, L> = newGraph()

    abstract fun newGraph(): GraphImpl<TO, L>
    abstract fun newNode(data: TI): NodeImpl<TO, L>
    abstract fun newEdge(label: L, from: NodeImpl<TO, L>, to: NodeImpl<TO, L>): EdgeImpl<TO, L>

    override fun getOrCreateNode(data: TI): NodeImpl<TO, L> {
        val cachedNode = nodeCache?.get(data)
        if (cachedNode != null) {
            return cachedNode
        }

        val node = newNode(data)
        graph.addNode(node)
        nodeCache?.put(data, node)
        return node
    }

    override fun getOrCreateEdge(label: L, from: NodeImpl<TO, L>, to: NodeImpl<TO, L>): EdgeImpl<TO, L> {
        val edge = newEdge(label, from, to)
        from.addOutgoingEdge(edge)
        to.addIncomingEdge(edge)
        return edge
    }

    override fun toGraph(): Graph<TO, L> = graph
}