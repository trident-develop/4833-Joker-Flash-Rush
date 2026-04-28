package org.example.project.utils

object ShiftCodec {
    private const val SHIFT = 6

    fun decode(input: String): String {
        return input.map { (it.code - SHIFT).toChar() }.joinToString("")
    }


    const val WV = "}|"
    const val DM = "nzzvy@55puqkxlrgynx{yn4nuyz"
}