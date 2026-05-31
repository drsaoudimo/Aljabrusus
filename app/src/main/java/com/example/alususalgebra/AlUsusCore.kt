package com.example.alususalgebra

import kotlin.math.sqrt

data class PrimeFactor(
    val prime: Long,
    val exponent: Int,
    val primeIndex: Int
)

data class AlUsusVector(
    val n: Long,
    val factors: List<PrimeFactor>
) {
    val vectorString: String
        get() {
            if (n <= 1) return "(0, 0, ...)"
            val maxIndex = factors.maxOfOrNull { it.primeIndex } ?: 0
            val vector = IntArray(maxIndex)
            for (f in factors) {
                vector[f.primeIndex - 1] = f.exponent
            }
            return "(" + vector.joinToString(", ") + ", 0, ...)"
        }
}

data class KasrNode(
    val prime: Long,
    val exponent: Int,
    val quotient: Long,
    val nextNode: KasrNode?
)

object AlUsusCore {
    val primes = listOf(
        2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71,
        73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151,
        157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233,
        239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307, 311, 313, 317,
        331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419,
        421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503,
        509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, 601, 607,
        613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691, 701,
        709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811,
        821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911,
        919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997
    )

    fun getPrimeAt(index: Int): Int {
        if (index <= 0) return 2
        if (index <= primes.size) {
            return primes[index - 1]
        }
        var count = primes.size
        var current = primes.last()
        while (count < index) {
            current += 2
            if (isPrime(current.toLong())) {
                count++
            }
        }
        return current
    }

    fun getPrimeIndex(prime: Long): Int {
        val found = primes.indexOf(prime.toInt())
        if (found != -1) return found + 1
        
        var count = primes.size
        var current = primes.last()
        if (prime < current) {
            for (i in primes.indices) {
                if (primes[i].toLong() == prime) return i + 1
            }
            return 1
        }
        
        while (current < prime) {
            current += 2
            if (isPrime(current.toLong())) {
                count++
                if (current.toLong() == prime) return count
            }
        }
        return count
    }

    fun isPrime(n: Long): Boolean {
        if (n <= 1) return false
        if (n <= 3) return true
        if (n % 2 == 0L || n % 3 == 0L) return false
        var i = 5L
        while (i * i <= n) {
            if (n % i == 0L || n % (i + 2) == 0L) return false
            i += 6
        }
        return true
    }

    fun factorize(n: Long): AlUsusVector {
        if (n <= 1) return AlUsusVector(n, emptyList())
        var temp = n
        val localFactors = mutableListOf<PrimeFactor>()
        
        if (temp % 2 == 0L) {
            var exp = 0
            while (temp % 2 == 0L) {
                exp++
                temp /= 2
            }
            localFactors.add(PrimeFactor(2L, exp, 1))
        }
        
        var divisor = 3L
        while (divisor * divisor <= temp) {
            if (temp % divisor == 0L) {
                var exp = 0
                while (temp % divisor == 0L) {
                    exp++
                    temp /= divisor
                }
                val idx = getPrimeIndex(divisor)
                localFactors.add(PrimeFactor(divisor, exp, idx))
            }
            divisor += 2
        }
        
        if (temp > 1) {
            val idx = getPrimeIndex(temp)
            localFactors.add(PrimeFactor(temp, 1, idx))
        }
        
        return AlUsusVector(n, localFactors.sortedBy { f -> f.primeIndex })
    }

    // الوهـج (Wahaj) total prime factors count
    fun computeWahaj(n: Long): Int {
        if (n <= 1) return 0
        return factorize(n).factors.sumOf { it.exponent }
    }

    // القـدرة (Qudra) product of factor exponents
    fun computeQudra(n: Long): Int {
        if (n <= 1) return 1
        val facs = factorize(n).factors
        if (facs.isEmpty()) return 1
        return facs.fold(1) { acc, f -> acc * f.exponent }
    }

    // العمـد (A'mad) largest prime pillar index
    fun computeAmad(n: Long): Int {
        if (n <= 1) return 0
        return factorize(n).factors.maxOfOrNull { it.primeIndex } ?: 0
    }

    // المعـول (Mi'wal) reciprocal of Qudra
    fun computeMiwal(n: Long): Double {
        val q = computeQudra(n)
        return if (q == 0) 1.0 else 1.0 / q
    }

    // الإشـعاع (Radiation)
    fun computeRadiation(n: Long): Int {
        return 2 * computeWahaj(n)
    }

    // الجـذر (Kernel) product of distinct prime factors
    fun computeGidr(n: Long): Long {
        if (n <= 1) return 1L
        return factorize(n).factors.fold(1L) { acc, f -> acc * f.prime }
    }

    // السـبحة (Sabaha) factor decomposition chain
    fun computeSabaha(n: Long): List<Pair<Long, Int>> {
        if (n <= 1) return emptyList()
        val vector = factorize(n)
        return vector.factors.map { Pair(it.prime, it.exponent) }
    }

    // الكسـر (Kasr) partition tree
    fun computeKasr(n: Long): KasrNode? {
        if (n <= 1 || isPrime(n)) return null
        val vector = factorize(n)
        if (vector.factors.isEmpty()) return null
        val first = vector.factors.first()
        var divisor = 1L
        for (i in 1..first.exponent) {
            divisor *= first.prime
        }
        val quotient = n / divisor
        return KasrNode(
            prime = first.prime,
            exponent = first.exponent,
            quotient = quotient,
            nextNode = computeKasr(quotient)
        )
    }

    // الرصـد (Rasad) shadow calculation
    fun computeRasad(primeIndex: Int, n: Long): Long {
        val p = getPrimeAt(primeIndex).toLong()
        if (n == 0L) return 0L
        return p / n
    }

    // المسافة الأصولية (Al-Masafa Al-Usuliyya)
    fun computeMasafa(m: Long, n: Long): Int {
        val gcdVal = gcd(m, n)
        return computeWahaj(m) + computeWahaj(n) - 2 * computeWahaj(gcdVal)
    }

    private fun gcd(a: Long, b: Long): Long {
        var n1 = a
        var n2 = b
        while (n2 != 0L) {
            val temp = n2
            n2 = n1 % n2
            n1 = temp
        }
        return n1
    }

    // العكـس الأصولـي (Primal Conjugate) n*
    fun computeConjugation(n: Long): Long? {
        if (n <= 1) return 1L
        val vector = factorize(n)
        val limitIndex = vector.factors.maxOfOrNull { it.primeIndex } ?: 0
        var result = 1.0
        val factorMap = vector.factors.associate { it.primeIndex to it.exponent }
        
        for (idx in 1..limitIndex) {
            val prime = getPrimeAt(idx).toLong()
            val exp = factorMap[idx] ?: 0
            val exponentVal = Math.pow(prime.toDouble(), exp.toDouble())
            val term = Math.pow(prime.toDouble(), exponentVal)
            result *= term
            if (result.isInfinite() || result > Long.MAX_VALUE) {
                return null // Explosion state
            }
        }
        return result.toLong()
    }

    // العكـس الأصولـي المزاح (Shift Conjugator n°)
    fun computeModifiedConjugation(n: Long): Long {
        if (n <= 1) return 1L
        val vector = factorize(n)
        val limitIndex = vector.factors.maxOfOrNull { it.primeIndex } ?: 0
        if (limitIndex <= 1) return 1L
        
        val factorMap = vector.factors.associate { it.primeIndex to it.exponent }
        var result = 1.0
        for (idx in 1..(limitIndex - 1)) {
            val prime = getPrimeAt(idx).toLong()
            val originalNextExp = factorMap[idx + 1] ?: 0
            if (originalNextExp > 0) {
                val term = Math.pow(prime.toDouble(), originalNextExp.toDouble())
                result *= term
            }
        }
        if (result.isInfinite() || result > Long.MAX_VALUE) {
            return Long.MAX_VALUE
        }
        return result.toLong()
    }

    // صائد الأصول / النبضة التراكمية T(n) = sum_{k=1}^n Wahaj(k)
    fun computeCumulativePulse(n: Long): Long {
        var total = 0L
        for (k in 1..n) {
            total += computeWahaj(k)
        }
        return total
    }

    // Betti numbers for numbers representing Betti components
    // H0 = active components (clusters of divisibility paths)
    // H1 = holes (composite gaps)
    fun getNumberTopologyInfo(n: Long): NumberTopology {
        val w = computeWahaj(n)
        val q = computeQudra(n)
        val h0 = if (isPrime(n)) 1 else if (n <= 1) 1 else computeSabaha(n).size
        val h1 = if (isPrime(n)) 0 else (w - h0).coerceAtLeast(0)
        return NumberTopology(w, q, h0, h1)
    }

    // 1️⃣ النبض الأصولي (Nabad/Prime Resonance): exponent of p in n
    fun nabad(n: Long, p: Long): Int {
        if (n <= 0 || p <= 1) return 0
        var count = 0
        var temp = n
        while (temp % p == 0L) {
            count++
            temp /= p
        }
        return count
    }

    // 2️⃣ توليد الأعداد الأولية (Sieve of Eratosthenes)
    fun sievePrimes(limit: Int): List<Int> {
        if (limit < 2) return emptyList()
        val isPrimeVec = BooleanArray(limit + 1) { true }
        isPrimeVec[0] = false
        isPrimeVec[1] = false
        val s = sqrt(limit.toDouble()).toInt()
        for (p in 2..s) {
            if (isPrimeVec[p]) {
                for (i in p * p..limit step p) {
                    isPrimeVec[i] = false
                }
            }
        }
        val result = mutableListOf<Int>()
        for (p in 2..limit) {
            if (isPrimeVec[p]) {
                result.add(p)
            }
        }
        return result
    }

    // A1 Matrix Constant (19 rows, 6 columns) representing primal projection
    val A1 = arrayOf(
        longArrayOf(7, 286, 200, 176, 120, 165),
        longArrayOf(206, 75, 129, 109, 123, 111),
        longArrayOf(43, 52, 99, 128, 111, 110),
        longArrayOf(98, 135, 112, 78, 118, 64),
        longArrayOf(77, 227, 93, 88, 69, 60),
        longArrayOf(34, 30, 73, 54, 45, 83),
        longArrayOf(182, 88, 75, 85, 54, 53),
        longArrayOf(89, 59, 37, 35, 38, 29),
        longArrayOf(18, 45, 60, 49, 62, 55),
        longArrayOf(78, 96, 29, 22, 24, 13),
        longArrayOf(14, 11, 11, 18, 12, 12),
        longArrayOf(30, 52, 52, 44, 28, 28),
        longArrayOf(20, 56, 40, 31, 50, 40),
        longArrayOf(46, 42, 29, 19, 36, 25),
        longArrayOf(22, 17, 19, 26, 30, 20),
        longArrayOf(15, 21, 11, 8, 8, 19),
        longArrayOf(5, 8, 8, 11, 11, 8),
        longArrayOf(3, 9, 5, 4, 7, 3),
        longArrayOf(6, 3, 5, 4, 5, 6)
    )

    // Jacobi Symmetric Eigenvalues Solver
    fun jacobiEigenvalues(matrix: Array<DoubleArray>, maxIterations: Int = 150): DoubleArray {
        val n = matrix.size
        val a = Array(n) { i -> matrix[i].clone() }
        
        for (iter in 0 until maxIterations) {
            var row = 0
            var col = 0
            var maxVal = 0.0
            for (i in 0 until n) {
                for (j in i + 1 until n) {
                    val absVal = Math.abs(a[i][j])
                    if (absVal > maxVal) {
                        maxVal = absVal
                        row = i
                        col = j
                    }
                }
            }
            
            if (maxVal < 1e-9) break
            
            val g = a[row][row]
            val h = a[col][col]
            val f = a[row][col]
            val diff = h - g
            val t: Double
            if (Math.abs(diff) < 1e-15) {
                t = if (f > 0) 1.0 else -1.0
            } else {
                val theta = 0.5 * diff / f
                var tempT = 1.0 / (Math.abs(theta) + Math.sqrt(1.0 + theta * theta))
                if (theta < 0) tempT = -tempT
                t = tempT
            }
            
            val c = 1.0 / Math.sqrt(1.0 + t * t)
            val s = t * c
            val tau = s / (1.0 + c)
            
            val a_row_row = g - t * f
            val a_col_col = h + t * f
            a[row][row] = a_row_row
            a[col][col] = a_col_col
            a[row][col] = 0.0
            a[col][row] = 0.0
            
            for (i in 0 until n) {
                if (i != row && i != col) {
                    val gR = a[i][row]
                    val hR = a[i][col]
                    a[i][row] = gR - s * (hR + gR * tau)
                    a[row][i] = a[i][row]
                    a[i][col] = hR + s * (gR - hR * tau)
                    a[col][i] = a[i][col]
                }
            }
        }
        
        val eigenvalues = DoubleArray(n) { i -> a[i][i] }
        eigenvalues.sort()
        return eigenvalues
    }
}

data class NumberTopology(
    val b0: Int, // Connectivity
    val b1: Int, // Loops/Cycles
    val h0: Int, // Connected Components of meanings
    val h1: Int  // Holes
)
