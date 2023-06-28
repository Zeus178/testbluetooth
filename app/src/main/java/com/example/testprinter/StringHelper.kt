package com.example.testprinter

object StringHelper {
    fun swapus(s: String): String {
        val u = "·129·"
        val U = "·154·"
        var ret = s
        for (j in 0 until checkCount(s, "ü")) {
            for (i in 0 until ret.length) {
                if (ret[i] == 'ü') {
                    ret = ret.substring(0, i) + u + ret.substring(i + 1, ret.length)
                }
            }
        }
        return ret
    }

    fun swapU(s: String): String {
        val u = "·129·"
        val U = "·154·"
        var ret = s
        for (j in 0 until checkCount(s, "Ü")) {
            for (i in 0 until ret.length) {
                if (ret[i] == 'Ü') {
                    ret = ret.substring(0, i) + U + ret.substring(i + 1, ret.length)
                }
            }
        }
        return ret
    }

    fun checkCount(s: String, checkString: String): Int {
//        int newPadding=paddingLeft;
        var count = 0
        var flag = 0
        for (i in 0 until s.length - checkString.length + 1) {
            if (s[i] == checkString[0]) {
                flag++
                for (j in 1 until checkString.length) {
                    if (s[i + j] == checkString[j]) {
                        flag++
                    }
                }
            }
            if (flag == checkString.length) {
                count += 1
            }
            flag = 0
        }
        return count
    }
}