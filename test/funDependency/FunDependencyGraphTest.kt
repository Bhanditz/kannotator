package funDependency

import java.io.File
import java.util.Comparator
import kotlin.test.fail
import kotlinlib.*
import org.jetbrains.kannotator.funDependecy.FunctionNode
import org.jetbrains.kannotator.funDependecy.buildFunctionDependencyGraph
import org.junit.Assert
import org.junit.Test
import org.objectweb.asm.ClassReader
import org.jetbrains.kannotator.index.FileBasedClassSource
import org.jetbrains.kannotator.index.DeclarationIndexImpl
import util.ClassPathDeclarationIndex
import org.jetbrains.kannotator.index.ClassSource
import util.ClassesFromClassPath

private val PATH = "testData/funDependency/"

class FunDependencyGraphTest {

    Test fun funInDifferentClassesTest() {
        doTest("funInDifferentClasses/funInDifferentClasses.txt", "fundependency.funInDifferentClasses.First", "fundependency.funInDifferentClasses.Second")
    }

    Test fun multiplyInvokeOfMethod() {
        doTest("multiplyInvokeOfMethod/multiplyInvokeOfMethod.txt", "fundependency.multiplyInvokeOfMethod.First", "fundependency.multiplyInvokeOfMethod.Second")
    }

    Test fun noAnnotatedMethods() {
        doTest("noAnnotatedMethods/noAnnotatedMethods.txt", "fundependency.noAnnotatedMethods.First")
    }

    Test fun recursiveFunTest() {
        doTest("recursiveFun/recursiveFun.txt", "fundependency.recursiveFun.First", "fundependency.recursiveFun.Second")
    }

    Test fun simpleTest() {
        doTest("simple/simple.txt", "fundependency.simple.Simple")
    }

    fun doTest(expectedResultPath: String, vararg canonicalNames: String) {
        val classSource = ClassesFromClassPath(*canonicalNames)
        val graph = buildFunctionDependencyGraph(ClassPathDeclarationIndex, classSource)

        val functionNodeComparator = object : Comparator<FunctionNode> {
            public override fun compare(o1: FunctionNode?, o2: FunctionNode?): Int {
                return o1?.method.toString().compareTo(o2?.method.toString())
            }

            public override fun equals(obj: Any?): Boolean {
                throw IllegalStateException()
            }
        }

        val actual = buildString { sb ->
            sb.println("== All Nodes == ")
            for (node in graph.functions.sort(functionNodeComparator)) {
                printFunctionNode(sb, node)
            }

            sb.println()
            sb.println("== No Outgoing Nodes == ")

            for (node in graph.noOutgoingNodes.sort(functionNodeComparator)) {
                printFunctionNode(sb, node)
            }
        }.trim()

        val expectedFile = File(PATH + expectedResultPath)
        if (!expectedFile.exists()) {
            expectedFile.writeText(actual)
            fail("Expected data file file does not exist: ${expectedFile}. It is created from actual data")
        }
        val expected = expectedFile.readText().trim()
        println(actual)
        println()

        Assert.assertEquals(expected, actual)
    }

    fun printFunctionNode(sb: StringBuilder, node: FunctionNode) {
        sb.println(node.method)
        if (node.outgoingEdges.size() > 0) sb.println("    outgoing edges:")
        for (edge in node.outgoingEdges.sortByToString()) {
            sb.println("        $edge")
        }

        if (node.incomingEdges.size() > 0) sb.println("    incoming edges:")
        for (edge in node.incomingEdges.sortByToString()) {
            sb.println("        $edge")
        }
    }
}

