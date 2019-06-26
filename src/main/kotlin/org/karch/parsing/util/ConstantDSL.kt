package org.karch.parsing.util

interface ConstantSpecificationContext<TypeOfConstant : Number> {
    infix fun String.size(size: TypeOfConstant)
}

class IntegerConstantSpecificationContext(
        var result: Int
) : ConstantSpecificationContext<Int> {

    override fun String.size(size: Int) {
        result += size
    }

}

fun constant(
        specificationBlock: ConstantSpecificationContext<Int>.()->Unit
): Int {
    val context = IntegerConstantSpecificationContext(0)
    context.specificationBlock()
    return context.result
}
