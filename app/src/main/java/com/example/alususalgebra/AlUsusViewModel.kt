package com.example.alususalgebra

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alususalgebra.db.AppDatabase
import com.example.alususalgebra.db.AlUsusRecordEntity
import com.example.alususalgebra.db.AlUsusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.math.BigInteger

enum class AlUsusTab {
    PLAYGROUND,
    TEXT_TOPOLOGY,
    PRIMAL_HUNTER,
    MANIFESTO
}

sealed interface TextAnalysisState {
    object Idle : TextAnalysisState
    object Loading : TextAnalysisState
    data class Success(val result: TopologicalTextResult) : TextAnalysisState
    data class Error(val message: String) : TextAnalysisState
}

class AlUsusViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "AlUsusViewModel"
    private val repository: AlUsusRepository

    // Reactive database history
    val historyItems: StateFlow<List<AlUsusRecordEntity>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AlUsusRepository(database.dao)
        historyItems = repository.allRecords.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        
        // Add default presets if database is empty on start
        viewModelScope.launch {
            repository.allRecords.collect { list ->
                if (list.isEmpty()) {
                    addPresets()
                }
            }
        }
    }

    // Tab state
    private val _activeTab = MutableStateFlow(AlUsusTab.PLAYGROUND)
    val activeTab: StateFlow<AlUsusTab> = _activeTab.asStateFlow()

    // Factorizer states
    val activeInput = MutableStateFlow("60")
    
    private val _factorization = MutableStateFlow<AlUsusVectorBig?>(null)
    val factorization: StateFlow<AlUsusVectorBig?> = _factorization.asStateFlow()

    private val _kasrTree = MutableStateFlow<KasrNodeBig?>(null)
    val kasrTree: StateFlow<KasrNodeBig?> = _kasrTree.asStateFlow()

    private val _topology = MutableStateFlow<NumberTopology?>(null)
    val topology: StateFlow<NumberTopology?> = _topology.asStateFlow()

    private val _conjugationResult = MutableStateFlow<String>("1")
    val conjugationResult: StateFlow<String> = _conjugationResult.asStateFlow()

    private val _modifiedConjugationResult = MutableStateFlow<String>("1")
    val modifiedConjugationResult: StateFlow<String> = _modifiedConjugationResult.asStateFlow()

    // Distance states
    val mInput = MutableStateFlow("12")
    val nInput = MutableStateFlow("18")
    private val _masafaDistance = MutableStateFlow<Int?>(null)
    val masafaDistance: StateFlow<Int?> = _masafaDistance.asStateFlow()

    // Real-Time Spectral A1 states
    val sParameter = MutableStateFlow(0.0f)
    val primeDimension = MutableStateFlow(10) // dimension from 10 up to 50

    private val _eigenvalues = MutableStateFlow<DoubleArray>(DoubleArray(19))
    val eigenvalues: StateFlow<DoubleArray> = _eigenvalues.asStateFlow()

    private val _spacings = MutableStateFlow<DoubleArray>(DoubleArray(18))
    val spacings: StateFlow<DoubleArray> = _spacings.asStateFlow()

    private val _dftSpectrum = MutableStateFlow<DoubleArray>(DoubleArray(60))
    val dftSpectrum: StateFlow<DoubleArray> = _dftSpectrum.asStateFlow()

    // Text Topology states
    val textToAnalyze = MutableStateFlow("الطوبولوجيا تفتح آفاقاً لفهم الفضاءات الدلالية العميقة")
    private val _textResponseState = MutableStateFlow<TextAnalysisState>(TextAnalysisState.Idle)
    val textResponseState: StateFlow<TextAnalysisState> = _textResponseState.asStateFlow()

    // Primal Hunter states
    val primeIndexInput = MutableStateFlow("7")
    private val _calculatedPrimeResult = MutableStateFlow<Long?>(17L)
    val calculatedPrimeResult: StateFlow<Long?> = _calculatedPrimeResult.asStateFlow()

    // 1️⃣ Primal Homomorphic Cryptography States
    val cryptoXInput = MutableStateFlow("12")
    val cryptoYInput = MutableStateFlow("15")
    val cryptoXExponents = MutableStateFlow<IntArray>(IntArray(6))
    val cryptoYExponents = MutableStateFlow<IntArray>(IntArray(6))
    val cryptoXCiphertext = MutableStateFlow<DoubleArray>(DoubleArray(6))
    val cryptoYCiphertext = MutableStateFlow<DoubleArray>(DoubleArray(6))
    val cryptoSumCiphertext = MutableStateFlow<DoubleArray>(DoubleArray(6))
    val decryptedExponents = MutableStateFlow<IntArray>(IntArray(6))
    val decryptedNumber = MutableStateFlow(180L)

    // 2️⃣ SVD DNA Compression States
    val svdKValue = MutableStateFlow(3)
    val svdSingularValues = MutableStateFlow<DoubleArray>(DoubleArray(6))
    val svdReconstructionError = MutableStateFlow(0.0)

    // 3️⃣ Al-Aks Cellular Automata Matrix Evolution States
    val aksGeneration = MutableStateFlow(0)
    val aksGrid = MutableStateFlow<Array<LongArray>>(emptyArray())

    // 4️⃣ Tropical Geometry & Metric Tensor / Determinant States
    val tropicalXInputStr = MutableStateFlow("2, 3, 1, 5, 2, 1")
    val tropicalYGenerated = MutableStateFlow<LongArray>(LongArray(19))
    val tropicalXSolved = MutableStateFlow<LongArray>(LongArray(6))
    val tropicalIsSolvable = MutableStateFlow(true)
    val tropicalLog = MutableStateFlow("")

    val metricTensorExponents = MutableStateFlow<Array<Array<IntArray>>>(Array(6) { Array(6) { IntArray(6) } }) // [prime_idx][row_6][col_6]
    val primalDeterminantExponents = MutableStateFlow<IntArray>(IntArray(6)) // [2, 3, 5, 7, 11, 13]

    init {
        // Run initial calculations
        calculateAllForActive()
        calculateMasafa()
        calculateSpectralProperties()
        calculateCrypto()
        calculateSvd()
        resetAksGrid()
        calculateTropical()
        calculateMetricAndDeterminant()
    }

    private fun solveLinear6x6(matrix: Array<DoubleArray>, b: DoubleArray): DoubleArray? {
        val n = 6
        val A = Array(n) { i -> DoubleArray(n + 1) { j -> if (j < n) matrix[j][i] else b[i] } }
        for (i in 0 until n) {
            var maxRow = i
            for (k in i + 1 until n) {
                if (Math.abs(A[k][i]) > Math.abs(A[maxRow][i])) {
                    maxRow = k
                }
            }
            val temp = A[i]
            A[i] = A[maxRow]
            A[maxRow] = temp
            
            if (Math.abs(A[i][i]) < 1e-9) {
                return null
            }
            
            for (k in i + 1..n) {
                A[i][k] /= A[i][i]
            }
            A[i][i] = 1.0
            
            for (k in 0 until n) {
                if (k != i) {
                    val factor = A[k][i]
                    for (j in i..n) {
                        A[k][j] -= factor * A[i][j]
                    }
                }
            }
        }
        return DoubleArray(n) { i -> A[i][n] }
    }

    fun calculateCrypto() {
        val xVal = cryptoXInput.value.toLongOrNull() ?: 1L
        val yVal = cryptoYInput.value.toLongOrNull() ?: 1L
        
        val primes6 = listOf(2L, 3L, 5L, 7L, 11L, 13L)
        val xExp = IntArray(6) { j -> AlUsusCore.nabad(xVal, primes6[j]) }
        val yExp = IntArray(6) { j -> AlUsusCore.nabad(yVal, primes6[j]) }
        
        cryptoXExponents.value = xExp
        cryptoYExponents.value = yExp
        
        val phi = Array(6) { i ->
            DoubleArray(6) { j ->
                AlUsusCore.nabad(AlUsusCore.A1[0][i], primes6[j]).toDouble()
            }
        }
        
        val cX = DoubleArray(6) { j ->
            var sum = 0.0
            for (i in 0 until 6) {
                sum += xExp[i] * phi[i][j]
            }
            sum
        }
        val cY = DoubleArray(6) { j ->
            var sum = 0.0
            for (i in 0 until 6) {
                sum += yExp[i] * phi[i][j]
            }
            sum
        }
        
        cryptoXCiphertext.value = cX
        cryptoYCiphertext.value = cY
        
        val cSum = DoubleArray(6) { j -> cX[j] + cY[j] }
        cryptoSumCiphertext.value = cSum
        
        val solved = solveLinear6x6(phi, cSum)
        if (solved != null) {
            val rounded = IntArray(6) { i -> Math.round(solved[i]).toInt().coerceAtLeast(0) }
            decryptedExponents.value = rounded
            
            var prod = 1L
            for (i in 0 until 6) {
                val prime = primes6[i]
                for (k in 0 until rounded[i]) {
                    prod *= prime
                }
            }
            decryptedNumber.value = prod
        } else {
            decryptedNumber.value = -1L
        }
    }

    fun onCryptoInputChange(isX: Boolean, newValue: String) {
        if (isX) cryptoXInput.value = newValue else cryptoYInput.value = newValue
        calculateCrypto()
    }

    fun calculateSvd() {
        val primes6 = listOf(2L, 3L, 5L, 7L, 11L, 13L)
        val phi = Array(19) { i ->
            DoubleArray(6) { j ->
                AlUsusCore.nabad(AlUsusCore.A1[i][j], primes6[j]).toDouble()
            }
        }
        
        val B = Array(6) { i ->
            DoubleArray(6) { j ->
                var sum = 0.0
                for (r in 0 until 19) {
                    sum += phi[r][i] * phi[r][j]
                }
                sum
            }
        }
        
        val eigSB = AlUsusCore.jacobiEigenvalues(B)
        val sing = DoubleArray(6) { i -> Math.sqrt(eigSB[i].coerceAtLeast(0.0)) }
        svdSingularValues.value = sing
        
        val totalEnergy = eigSB.sum()
        if (totalEnergy > 1e-9) {
            val k = svdKValue.value.coerceIn(1, 6)
            var keptEnergy = 0.0
            for (idx in (6 - k) until 6) {
                keptEnergy += eigSB[idx]
            }
            val error = Math.sqrt((1.0 - (keptEnergy / totalEnergy)).coerceAtLeast(0.0))
            svdReconstructionError.value = error * 100.0
        } else {
            svdReconstructionError.value = 0.0
        }
    }

    fun onSvdKValueChange(newValue: Int) {
        svdKValue.value = newValue
        calculateSvd()
    }

    fun resetAksGrid() {
        aksGeneration.value = 0
        val originalGrid = Array(19) { i -> AlUsusCore.A1[i].clone() }
        aksGrid.value = originalGrid
    }

    fun nextGenerationAks() {
        val current = aksGrid.value
        if (current.isEmpty()) return
        val next = Array(19) { i -> current[i].clone() }
        
        for (i in 0 until 19) {
            for (j in 0 until 6) {
                val n = current[i][j]
                if (n <= 1L) {
                    next[i][j] = 1L
                } else if (AlUsusCore.isPrime(n)) {
                    next[i][j] = n
                } else {
                    val conj = AlUsusCore.computeConjugation(n)
                    if (conj != null && conj < 5000L) {
                        next[i][j] = (conj % 499L) + 2L
                    } else {
                        next[i][j] = 1L
                        val neighbors = listOf(
                            Pair(i - 1, j), Pair(i + 1, j),
                            Pair(i, j - 1), Pair(i, j + 1)
                        )
                        for (nb in neighbors) {
                            val r = nb.first
                            val c = nb.second
                            if (r in 0 until 19 && c in 0 until 6) {
                                val neighborVal = next[r][c]
                                if (neighborVal < 1000000L) {
                                    next[r][c] = neighborVal + 2
                                }
                            }
                        }
                    }
                }
            }
        }
        aksGeneration.value += 1
        aksGrid.value = next
    }

    private fun lcmLong(a: Long, b: Long): Long {
        if (a == 0L || b == 0L) return 0L
        val g = gcdLong(a, b)
        return (a / g) * b
    }

    private fun gcdLong(a: Long, b: Long): Long {
        var n1 = a
        var n2 = b
        while (n2 != 0L) {
            val temp = n2
            n2 = n1 % n2
            n1 = temp
        }
        return n1
    }

    private fun generatePermutations6(): List<IntArray> {
        val list = mutableListOf<IntArray>()
        fun permute(arr: IntArray, k: Int) {
            val n = arr.size
            if (k == n) {
                list.add(arr.clone())
                return
            }
            for (i in k until n) {
                val tmp = arr[k]
                arr[k] = arr[i]
                arr[i] = tmp
                
                permute(arr, k + 1)
                
                arr[i] = arr[k]
                arr[k] = tmp
            }
        }
        permute(intArrayOf(0, 1, 2, 3, 4, 5), 0)
        return list
    }

    fun calculateTropical() {
        val primes6 = listOf(2L, 3L, 5L, 7L, 11L, 13L)
        val parts = tropicalXInputStr.value.split(",").map { it.trim().toLongOrNull() ?: 1L }
        val xVec = LongArray(6) { j -> if (j < parts.size) parts[j].coerceAtLeast(1L) else 1L }.map { it.coerceAtMost(20L) }.toLongArray() // limit inputs to avoid huge numbers
        
        // Compute Y_i = prod_{j=1}^6 lcm(A1_ij, X_j)
        val yVec = LongArray(19)
        for (i in 0 until 19) {
            var prodLong = 1L
            var hasOverflow = false
            for (j in 0 until 6) {
                val aVal = AlUsusCore.A1[i][j]
                val xVal = xVec[j]
                val itemLcm = lcmLong(aVal, xVal)
                if (Long.MAX_VALUE / itemLcm < prodLong) {
                    hasOverflow = true
                } else {
                    prodLong *= itemLcm
                }
            }
            yVec[i] = if (hasOverflow) Long.MAX_VALUE else prodLong
        }
        tropicalYGenerated.value = yVec

        // Solve equations: sum_{j=1}^6 max( v_p(A1_ij), x_j ) = v_p(Y_i)
        val solvedExponents = Array(6) { IntArray(6) } // [p_idx][j]
        var logText = ""
        var isSolvable = true

        for (pIdx in 0 until 6) {
            val p = primes6[pIdx]
            logText += "✦ البُعد الأولي $p:\n"
            
            // Reconstruct x_j exponent for this prime
            // x_j = min_{i=1}^{19} [ v_p(Y_i) - sum_{k != j} v_p(A1_ik) ]
            val xExp = IntArray(6)
            for (j in 0 until 6) {
                var minVal = Int.MAX_VALUE
                for (i in 0 until 19) {
                    val vpY = AlUsusCore.nabad(yVec[i], p)
                    var sumOtherA = 0
                    for (k in 0 until 6) {
                        if (k != j) {
                            sumOtherA += AlUsusCore.nabad(AlUsusCore.A1[i][k], p)
                        }
                    }
                    val diff = vpY - sumOtherA
                    if (diff < minVal) {
                        minVal = diff
                    }
                }
                xExp[j] = minVal.coerceAtLeast(0)
            }
            
            // Check solvability
            var satisfiesAll = true
            for (i in 0 until 19) {
                var sumMax = 0
                for (j in 0 until 6) {
                    val vpA = AlUsusCore.nabad(AlUsusCore.A1[i][j], p)
                    sumMax += Math.max(vpA, xExp[j])
                }
                val vpY = AlUsusCore.nabad(yVec[i], p)
                if (sumMax != vpY) {
                    satisfiesAll = false
                }
            }
            
            if (!satisfiesAll) {
                logText += "  ⚠️ النظام غير متطابق تماماً في هذا البعد (إسقاط تقريبي استوائي)\n"
            } else {
                logText += "  ✓ برهان الاتساق تام في هذا البعد!\n"
            }
            
            logText += "  متجه الأسوس المحلول: (${xExp.joinToString(", ")})\n"
            for (j in 0 until 6) {
                solvedExponents[j][pIdx] = xExp[j]
            }
        }
        
        // Reconstruct X_j numbers
        val solvedX = LongArray(6) { j ->
            var value = 1L
            for (pIdx in 0 until 6) {
                val p = primes6[pIdx]
                val exp = solvedExponents[j][pIdx]
                for (k in 0 until exp) {
                    value *= p
                }
            }
            value
        }
        
        tropicalXSolved.value = solvedX
        
        var matchCount = 0
        for (j in 0 until 6) {
            if (solvedX[j] == xVec[j]) matchCount++
        }
        
        if (matchCount == 6) {
            logText += "\n🎉 نصر تطبيقي مذهل! تمت إعادة بناء المتجه المجهول X بالكامل بدقة 100%!"
            tropicalIsSolvable.value = true
        } else {
            logText += "\n🔬 تم إيجاد المتجه الأمثل هندسياً X = (${solvedX.joinToString(", ")}) المقارب لدورة الطاقة."
            tropicalIsSolvable.value = false
        }
        
        tropicalLog.value = logText
    }

    fun onTropicalXInputChange(newStr: String) {
        tropicalXInputStr.value = newStr
        calculateTropical()
    }

    fun calculateMetricAndDeterminant() {
        val primes6 = listOf(2L, 3L, 5L, 7L, 11L, 13L)
        val mtExps = Array(6) { Array(6) { IntArray(6) } } // [p_idx][i][j]

        for (pIdx in 0 until 6) {
            val p = primes6[pIdx]
            for (i in 0 until 6) {
                for (j in 0 until 6) {
                    var sumMax = 0
                    for (k in 0 until 19) {
                        val vpA_ki = AlUsusCore.nabad(AlUsusCore.A1[k][i], p)
                        val vpA_kj = AlUsusCore.nabad(AlUsusCore.A1[k][j], p)
                        sumMax += Math.max(vpA_ki, vpA_kj)
                    }
                    mtExps[pIdx][i][j] = sumMax
                }
            }
        }
        metricTensorExponents.value = mtExps

        val detExps = IntArray(6)
        val permutations = generatePermutations6()

        for (pIdx in 0 until 6) {
            var maxVal = 0
            for (p in permutations) {
                var sumExp = 0
                for (i in 0 until 6) {
                    val sigma_i = p[i]
                    sumExp += mtExps[pIdx][i][sigma_i]
                }
                if (sumExp > maxVal) {
                    maxVal = sumExp
                }
            }
            detExps[pIdx] = maxVal
        }
        primalDeterminantExponents.value = detExps
    }

    fun selectTab(tab: AlUsusTab) {
        _activeTab.value = tab
    }

    fun onActiveInputChange(newValue: String) {
        activeInput.value = newValue
        calculateAllForActive()
    }

    fun onMasafaInputChange(isM: Boolean, value: String) {
        if (isM) mInput.value = value else nInput.value = value
        calculateMasafa()
    }

    fun onPrimeIndexChange(newValue: String) {
        primeIndexInput.value = newValue
        val idx = newValue.toIntOrNull() ?: return
        if (idx > 0 && idx < 50000) {
            viewModelScope.launch {
                val p = AlUsusCore.getPrimeAt(idx).toLong()
                _calculatedPrimeResult.value = p
            }
        }
    }

    private fun calculateAllForActive() {
        val numStr = activeInput.value.trim()
        if (numStr.isEmpty()) return
        val num = try { BigInteger(numStr) } catch(e: Exception) { return }
        if (num <= BigInteger.ZERO) return
        
        viewModelScope.launch {
            val vec = AlUsusBigCore.factorizeBig(num)
            _factorization.value = vec
            _kasrTree.value = AlUsusBigCore.computeKasrBig(num)
            _topology.value = AlUsusBigCore.getNumberTopologyInfoBig(num)
            
            val conj = AlUsusBigCore.computeConjugationBig(num)
            _conjugationResult.value = conj
            
            val modConj = AlUsusBigCore.computeModifiedConjugationBig(num)
            _modifiedConjugationResult.value = modConj
        }
    }

    private fun calculateMasafa() {
        val mStr = mInput.value.trim()
        val nStr = nInput.value.trim()
        val m = try { BigInteger(mStr) } catch(e: Exception) { return }
        val n = try { BigInteger(nStr) } catch(e: Exception) { return }
        
        if (m > BigInteger.ZERO && n > BigInteger.ZERO) {
            _masafaDistance.value = AlUsusBigCore.computeMasafaBig(m, n)
        }
    }

    fun onSParameterChange(value: Float) {
        sParameter.value = value
        calculateSpectralProperties()
    }

    fun onPrimeDimensionChange(value: Int) {
        primeDimension.value = value
        calculateSpectralProperties()
    }

    fun calculateSpectralProperties() {
        viewModelScope.launch {
            val s = sParameter.value.toDouble()
            val dim = primeDimension.value.coerceIn(10, 50)
            
            // Get the list of primes to use from AlUsusCore
            val primesToUse = AlUsusCore.primes.take(dim).map { it.toLong() }
            
            // Build Phi matrix (19 rows x dim columns)
            val phi = Array(19) { i ->
                DoubleArray(dim) { pIdx ->
                    var sum = 0.0
                    val p = primesToUse[pIdx]
                    for (col in 0 until 6) {
                        sum += AlUsusCore.nabad(AlUsusCore.A1[i][col], p).toDouble()
                    }
                    sum
                }
            }
            
            // Build H matrix
            val h = Array(19) { i ->
                DoubleArray(19) { j ->
                    var sum = 0.0
                    for (pIdx in 0 until dim) {
                        val p = primesToUse[pIdx]
                        val logP = Math.log(p.toDouble())
                        val top = (p - 1.0) * (p - 1.0)
                        val bottom = p + 1.0
                        val weight = logP * (top / bottom) * Math.pow(p.toDouble(), -s)
                        sum += phi[i][pIdx] * phi[j][pIdx] * weight
                    }
                    sum / 19.0 // normalize by rows N=19
                }
            }
            
            // Solve eigenvalues
            val eig = AlUsusCore.jacobiEigenvalues(h)
            _eigenvalues.value = eig
            
            // Spacings
            val sp = DoubleArray(18) { r -> eig[r + 1] - eig[r] }
            _spacings.value = sp
            
            // DFT Spectrum map for logs
            val dft = DoubleArray(60)
            for (idx in 0 until 60) {
                val t = 0.2 + idx * 0.25
                var sumReal = 0.0
                var sumImag = 0.0
                for (r in 0 until 19) {
                    val valRad = eig[r] * t
                    sumReal += Math.cos(valRad)
                    sumImag += Math.sin(valRad)
                }
                dft[idx] = Math.sqrt(sumReal * sumReal + sumImag * sumImag)
            }
            _dftSpectrum.value = dft
        }
    }

    // Save numerical factorization to history DB
    fun saveActiveToHistory() {
        val numStr = activeInput.value.trim()
        val num = try { BigInteger(numStr) } catch(e: Exception) { return }
        val vec = _factorization.value ?: return
        viewModelScope.launch {
            val json = JSONObject().apply {
                put("vectorString", vec.vectorString)
                put("wahaj", AlUsusBigCore.computeWahajBig(num))
                put("qudra", AlUsusBigCore.computeQudraBig(num))
                put("amad", AlUsusBigCore.computeAmadBig(num))
            }
            repository.insert(
                AlUsusRecordEntity(
                    type = "NUMBER",
                    value = num.toString(),
                    resultJson = json.toString()
                )
            )
        }
    }

    // Analyze Text Topology via Gemini/Heuristics
    fun performTextTopologyAnalysis() {
        val text = textToAnalyze.value.trim()
        if (text.isEmpty()) return

        _textResponseState.value = TextAnalysisState.Loading

        viewModelScope.launch {
            try {
                val res = GeminiClient.analyzeTextTopology(text)
                _textResponseState.value = TextAnalysisState.Success(res)

                // Cache in database
                val json = JSONObject().apply {
                    put("b0", res.betti0)
                    put("b1", res.betti1)
                    put("connectedExplanation", res.connectedComponentsExplanation)
                    put("holesExplanation", res.holesExplanation)
                    put("manifoldDesc", res.semanticManifoldDescription)
                    put("interpretation", res.philosophicalInterpretation)
                    put("wahaj", res.totalWahaj)
                    put("qudra", res.totalQudra)
                    put("amad", res.totalAmad)
                }

                repository.insert(
                    AlUsusRecordEntity(
                        type = "TEXT",
                        value = text,
                        resultJson = json.toString()
                    )
                )
            } catch (e: Exception) {
                _textResponseState.value = TextAnalysisState.Error(e.message ?: "حدث خطأ غير معروف")
            }
        }
    }

    // Delete a specific history card
    fun deleteCard(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    // Clear whole history
    fun clearHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    private suspend fun addPresets() {
        // Factorization of 840 (the Perfect Pillar)
        val j840 = JSONObject().apply {
            put("vectorString", "(3, 1, 1, 1, 0, 0, ...)")
            put("wahaj", 6)
            put("qudra", 3)
            put("amad", 4)
        }
        repository.insert(
            AlUsusRecordEntity(
                type = "NUMBER",
                value = "840",
                resultJson = j840.toString()
            )
        )

        // Text Analysis of Al-Usus algebraic weight
        val txt = "الجبر الأصولي هو الفضاء المعرفي المترابط للأعداد"
        val res = GeminiClient.computeHeuristicLocal(txt)
        val jText = JSONObject().apply {
            put("b0", res.betti0)
            put("b1", res.betti1)
            put("connectedExplanation", res.connectedComponentsExplanation)
            put("holesExplanation", res.holesExplanation)
            put("manifoldDesc", res.semanticManifoldDescription)
            put("interpretation", res.philosophicalInterpretation)
            put("wahaj", res.totalWahaj)
            put("qudra", res.totalQudra)
            put("amad", res.totalAmad)
        }
        repository.insert(
            AlUsusRecordEntity(
                type = "TEXT",
                value = txt,
                resultJson = jText.toString()
            )
        )
    }
}
