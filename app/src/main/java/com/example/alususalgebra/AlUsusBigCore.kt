package com.example.alususalgebra

import java.math.BigInteger

data class PrimeFactorBig(
    val prime: BigInteger,
    val exponent: Int,
    val primeIndex: Int
)

data class AlUsusVectorBig(
    val n: BigInteger,
    val factors: List<PrimeFactorBig>
) {
    val vectorString: String
        get() {
            if (n <= BigInteger.ONE) return "(0, 0, ...)"
            val maxIndex = factors.maxOfOrNull { it.primeIndex } ?: 0
            val vector = IntArray(maxIndex)
            for (f in factors) {
                if (f.primeIndex > 0) {
                    vector[f.primeIndex - 1] = f.exponent
                }
            }
            return "(" + vector.joinToString(", ") + ", 0, ...)"
        }
}

data class KasrNodeBig(
    val prime: BigInteger,
    val exponent: Int,
    val quotient: BigInteger,
    val nextNode: KasrNodeBig?
)

object AlUsusBigCore {
    val TWO = BigInteger.valueOf(2)
    val THREE = BigInteger.valueOf(3)
    
    fun getPrimeIndexBig(prime: BigInteger): Int {
        if (prime <= BigInteger.valueOf(Int.MAX_VALUE.toLong())) {
            val longVal = prime.toLong()
            return AlUsusCore.getPrimeIndex(longVal)
        }
        return -1 // Too big to index safely in real-time
    }

    fun factorizeBig(n: BigInteger): AlUsusVectorBig {
        if (n <= BigInteger.ONE) return AlUsusVectorBig(n, emptyList())
        if (n.isProbablePrime(20)) {
            val idx = getPrimeIndexBig(n)
            return AlUsusVectorBig(n, listOf(PrimeFactorBig(n, 1, if (idx > 0) idx else 9999)))
        }

        var temp = n
        val localFactors = mutableListOf<PrimeFactorBig>()
        
        if (temp.remainder(TWO) == BigInteger.ZERO) {
            var exp = 0
            while (temp.remainder(TWO) == BigInteger.ZERO) {
                exp++
                temp /= TWO
            }
            localFactors.add(PrimeFactorBig(TWO, exp, 1))
        }
        
        var divisor = THREE
        val startTime = System.currentTimeMillis()
        
        while (divisor * divisor <= temp) {
            // Smoothness safeguard for huge composites (NP-hard factorization)
            if (System.currentTimeMillis() - startTime > 1500) {
                localFactors.add(PrimeFactorBig(temp, 1, 9999)) // 9999 acts as an overflow index
                temp = BigInteger.ONE
                break
            }
            
            if (temp.remainder(divisor) == BigInteger.ZERO) {
                var exp = 0
                while (temp.remainder(divisor) == BigInteger.ZERO) {
                    exp++
                    temp /= divisor
                }
                val idx = getPrimeIndexBig(divisor)
                localFactors.add(PrimeFactorBig(divisor, exp, if (idx > 0) idx else 9999))
            }
            divisor += TWO
            
            if (temp > BigInteger.ONE && temp.isProbablePrime(20)) {
                val idx = getPrimeIndexBig(temp)
                localFactors.add(PrimeFactorBig(temp, 1, if (idx > 0) idx else 9999))
                temp = BigInteger.ONE
                break
            }
        }
        
        if (temp > BigInteger.ONE) {
            val idx = getPrimeIndexBig(temp)
            localFactors.add(PrimeFactorBig(temp, 1, if (idx > 0) idx else 9999))
        }
        
        return AlUsusVectorBig(n, localFactors.sortedBy { f -> f.primeIndex })
    }

    fun computeWahajBig(n: BigInteger): Int {
        if (n <= BigInteger.ONE) return 0
        return factorizeBig(n).factors.sumOf { it.exponent }
    }

    fun computeQudraBig(n: BigInteger): Int {
        if (n <= BigInteger.ONE) return 1
        val facs = factorizeBig(n).factors
        if (facs.isEmpty()) return 1
        return facs.fold(1) { acc, f -> acc * f.exponent }
    }

    fun computeAmadBig(n: BigInteger): Int {
        if (n <= BigInteger.ONE) return 0
        return factorizeBig(n).factors.maxOfOrNull { it.primeIndex } ?: 0
    }

    fun computeMiwalBig(n: BigInteger): Double {
        val q = computeQudraBig(n)
        return if (q == 0) 1.0 else 1.0 / q
    }

    fun computeGidrBig(n: BigInteger): BigInteger {
        if (n <= BigInteger.ONE) return BigInteger.ONE
        return factorizeBig(n).factors.fold(BigInteger.ONE) { acc, f -> acc * f.prime }
    }

    fun computeSabahaBig(n: BigInteger): List<Pair<BigInteger, Int>> {
        if (n <= BigInteger.ONE) return emptyList()
        val vector = factorizeBig(n)
        return vector.factors.map { Pair(it.prime, it.exponent) }
    }

    fun computeKasrBig(n: BigInteger): KasrNodeBig? {
        if (n <= BigInteger.ONE || n.isProbablePrime(20)) return null
        val vector = factorizeBig(n)
        if (vector.factors.isEmpty()) return null
        val first = vector.factors.first()
        var divisor = BigInteger.ONE
        for (i in 1..first.exponent) {
            divisor *= first.prime
        }
        val quotient = n / divisor
        return KasrNodeBig(
            prime = first.prime,
            exponent = first.exponent,
            quotient = quotient,
            nextNode = computeKasrBig(quotient)
        )
    }

    fun computeMasafaBig(m: BigInteger, n: BigInteger): Int {
        val gcdVal = m.gcd(n)
        return computeWahajBig(m) + computeWahajBig(n) - 2 * computeWahajBig(gcdVal)
    }

    fun computeConjugationBig(n: BigInteger): String {
        if (n <= BigInteger.ONE) return "1"
        val vector = factorizeBig(n)
        val limitIndex = vector.factors.maxOfOrNull { it.primeIndex } ?: 0
        if (limitIndex > 100) return "💥 انفجار طاقي إلى اللانهاية (محور ضخم جدًا)"
        var result = 1.0
        val factorMap = vector.factors.associate { it.primeIndex to it.exponent }
        
        for (idx in 1..limitIndex) {
            val prime = AlUsusCore.getPrimeAt(idx).toDouble()
            val exp = factorMap[idx] ?: 0
            val exponentVal = Math.pow(prime, exp.toDouble())
            val term = Math.pow(prime, exponentVal)
            result *= term
            if (result.isInfinite() || result > Double.MAX_VALUE) {
                return "💥 انفجار طاقي إلى اللانهاية (>9.2 × 10¹⁸)"
            }
        }
        return String.format(java.util.Locale.US, "%.0f", result)
    }

    fun computeModifiedConjugationBig(n: BigInteger): String {
        if (n <= BigInteger.ONE) return "1"
        val vector = factorizeBig(n)
        val limitIndex = vector.factors.maxOfOrNull { it.primeIndex } ?: 0
        if (limitIndex <= 1) return "1"
        if (limitIndex > 100) return "∞ مستقر ضخم"
        
        val factorMap = vector.factors.associate { it.primeIndex to it.exponent }
        var result = 1.0
        for (idx in 1..(limitIndex - 1)) {
            val prime = AlUsusCore.getPrimeAt(idx).toDouble()
            val originalNextExp = factorMap[idx + 1] ?: 0
            if (originalNextExp > 0) {
                val term = Math.pow(prime, originalNextExp.toDouble())
                result *= term
            }
            if (result.isInfinite() || result > Double.MAX_VALUE) {
                return "∞"
            }
        }
        return String.format(java.util.Locale.US, "%.0f", result)
    }

    fun getNumberTopologyInfoBig(n: BigInteger): NumberTopology {
        val w = computeWahajBig(n)
        val q = computeQudraBig(n)
        val isP = n > BigInteger.ONE && n.isProbablePrime(20)
        val h0 = if (isP) 1 else if (n <= BigInteger.ONE) 1 else computeSabahaBig(n).size
        val h1 = if (isP) 0 else (w - h0).coerceAtLeast(0)
        return NumberTopology(w, q, h0, h1)
    }
}
