package kin.kinoverflow.utils


fun removeXmlFormatting(originalString: String): String {
    val splitStringList = originalString.split(Regex("<.*?>"), 1000)

    return splitStringList.reduce(operation = { tags, tag ->
        return@reduce "$tags$tag"
    })
}