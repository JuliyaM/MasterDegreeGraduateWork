package main.java.prediction

import java.lang.RuntimeException


enum class ProcessKey(val russianName: String) {
    @Deprecated("Internal api")
    AnyProcess(""),
    Planing("Планирование"),
    Design("Проектирование архитектуры"),
    UiUx("Проектирование пользовательских интерфейсов"),
    Testing("Тестирование"),
    Delivery("Поставка"),
    Developing("Разработка"),
    Support("Сопровождение"),
    Documentation("Менеджмент програмной документации"),
    Removing("Утилизация")
}

//processes

//risks-causes

//-------------------------------------------------------------------------------
const val VIOLATION_PROJECT_TIME_PLAN = "Риск превышения затрат по ресурсам"

//all process
const val IMPORTANT_WORKER_ILL = "Болезнь ключевого сотрудника"
const val EMERGENCY = "Чрезвычайное происшествие (стихийное/политическое/ и т.д.)"
const val VIOLATION_OF_PROCESS_PLAN = "Нарушение плана проведения процесса"
const val CONFLICTS_IN_TEAM = "Возникновение конфликтов в команде"

//planing
const val INVALID_TASKS_ESTIMATION = "Ошибки в оценке сложности задач"
const val INVALID_TASK_EXECUTION_ORDER = "Ошибки в выборе порядка выполнения задач"
const val INVALID_TASK_EXECUTORS = "Некорректный выбор исполнителей для выполнения задач"
const val BAD_TECHNICAL_EQUIPMENT = "Плохое техническое оснащение команды"
const val LACK_TRAIN_SYSTEM = "Неэффективная система обучения исполнителей"
const val NON_STABLE_TECHNICAL_TASK = "Регулярные изменения начальных требований"

//design
const val ARCHITECTURE_OVERHEAD = "Излишние накладные расходы выбранной архитектуры"
const val REDESIGN_SYSTEM = "Необходимость перепроектирования архитектуры при небольших изменениях требований"
const val BAD_ARCHITECTURE_SCALING = "Плохое масштабирование архитектуры"
const val INVALID_DEVELOPER_LEVEL = "Отсутвие необходимых знаний у исполнителя для решения задачи"


//developing
const val TERMINATION_OF_LIBRARY_SUPPORT = "Прекращение поддержки библиотек, использованных при разработке"
const val BAD_DEVELOPER_PERFORMANCE = "Низкая производительность исполнителей"
const val BIG_COUNT_BUGS_ON_TEST =
    "Высокая доля некорректно работающего функционала, обнаруженного при тестировании"
const val BAD_TEAMS_COMMUNICATION = "Отсутствие согласованности между командами разработки"
const val BAD_ALGORITHM_COMPLEXITY =
    "Неэффективная реализация алгоритмов, требующая покупки дополнительных вычислительных мощностей"
const val BAD_DOCUMENTATION_DEVELOPING = "Усложнение разработки из-за некорректного документирования функционала"

// testing
const val BIG_FUNCTIONAL_CONNECTIVITY =
    "Необходимость проведения регулярного регрессионного тестирования из-за сильной связанности функционала"
const val BAD_DOCUMENTATION_TESTING = "Усложнение тестирования из-за некорректного документирования функционала"

//UiUx
const val INVALID_STANDARDS_USAGE =
    "Некорректное использование стандартов дизайна, влекущее за собой усложнение реализации элементов дизайна"
const val NOT_FULLY_ELABORATE_DESIGN =
    "Игнорирование особых кейсов при отрисовке дизайна (обработки ошибок, показа всплывающих сообщений)"


//-------------------------------------------------------------------------------
// risk - planing,developing,uiux
const val VIOLATION_RIGHT_HOLDER = "Риск конфликта с правообладателями"


//planing
const val NO_RIGHT_HOLDERS_DIALOG = "Отсутствие заключенных соглашений на использование собственности правообладателей"
const val INVALID_RIGHT_HOLDER_ANALYZE = "Некачественный анализ возможных нарушений прав правообладателей"
const val BAD_COMMUNICATION_WITH_CUSTOMER = "Проблемы коммуникации с заказчиком"

//developing
const val AUTHOR_CODE_USAGE = "Несогласованное использоване авторского кода правообладателей"
const val AUTOR_LIBRARIES_USAGE = "Несогласованное использование библиотек правообладателей при разработке"

//uiux
const val AUTHOR_MEDIA_USAGE = "Несогласованное использование медиа-файлов правообладателей"


//-------------------------------------------------------------------------------
const val PRODUCT_WORK_FAILURES = "Риск сбоя работы продукта у пользователей"

//testing
const val BIG_COUNT_BUGS_LOSS_ON_TEST =
    "Высокая доля некорректно работающего функционала, пропущенного при тестировании"
const val BAD_TESTING_ENVIRONMENT = "Отсутствие устройств, используемых пользователями"

//delivery
const val BAD_USER_ENVIRONMENT = "Некорректная установка продукта на конфигурацию устройств пользователя"

//developing
const val NON_STABLE_ALGORITHMS = "Некорректная реализация алгоритмов работающая нестабильно"
const val SITUATION_CODE = "Написание нестабильных инструкций, работающих только при определенных условиях"
const val BAD_HIGH_LOAD_REALISATION = "Отсутствие корректного использования алгоритмов работы при высоких нагрузках"
const val BAD_MULTI_THREADING = "Трудности многопоточной работы продукта"

//design
const val WRONG_ARCHITECTURE =
    "Выбор неподходящего архитектурного решения, работающего нестабильно для решения поставленной задачи"

//-------------------------------------------------------------------------------
//risk
const val MISMATCH_MARKET_CONDITIONS = "Риск несоответствия продукта рыночным условиям и требованиям"

//testing
const val IGNORE_BAD_PERFORMANCE = "Игнорирование проблем с дизайном и производительностью приложения при тестировании"
const val BAD_TEST_CASE = "Неправильный подбор тест кейсов, не учитывающих современные условия использования "

//developing
const val BAD_TECHNICAL_REALIZATION = "Низкокачественная реализация интерфейса или алгоримтов"
const val BAD_TECHNICAL_DEBT = "Игнорирование больших объемов технического долга продукта"
const val BAD_ALGORITHMS_LIBRARIES = "Использование устаревших алгоритмов и библиотек"

//ui_ux
const val INVALID_STANDARD_USAGE = "Игнорирование стандартов дизайна, принятых на платформах пользователей"

//planing
const val BAD_MARKET_ANALYZE = "Некачественный анализ рынка"
const val LACK_BUSINESS_EXPERT = "Отсутствие бизнес-специалиста в команде, разбирающегося в данной сфере"
const val BAD_UPDATE_POLITICS = "Отсутствие плана по регулярным обновлениям продукта"


//-------------------------------------------------------------------------------
//risk
const val BAD_USER_EXPERIENCE = "Риск негативной оценки продукта пользователями"

//delivery
const val NOT_FULLY_ENVIRONMENT = "Отсуствие поддержки устройств пользователей"
const val USER_ENV_ACCESS = "Невозможность быстро получить доступ к устройствам пользователей и отладить проблему"

//testing
const val BAD_FOUND_BUGS_TIME = "Низкая скорость выявления проблем пользователей"
const val UNEXPECTED_SYSTEM_STATE =
    "Невозможность повторить ошибку пользователя из-за неизвестного состояния продукта в момент ошибки"

//developing
const val BAD_ALGORITHM_PERFORMANCE = "Низкая эффективность оптимизации алгоритмов под платформу пользователя"


//ui_ux
const val HARD_UX_NO_HELP = "Высокая сложность работы продукта и отсутствие инструкций по его использованию"

//planing
const val IGNORING_USER_REVIEWS = "Игнорирование обратной связи от заинтересованных сторон"
const val BAD_MARKETING_STRATEGY = "Неправильная позицирование продукта для пользователей"
const val NON_CONSISTENT_FUNCTIONAL_OF_DIFFERENT_PLATFORMS =
    "Не согласованность выпускаемого функционала на разных поддерживаемых платформах"


@DslMarker
annotation class RiskMarkerTag

@DslMarker
annotation class ProcessMarkerTag

@DslMarker
annotation class RiskCauseauseMarkerTag


class PredictionProcessModel(
    override val key: ProcessKey
) : ContainerPredictionModel<String, ProcessKey, PredictionRiskModel>() {
    @RiskMarkerTag
    fun risk(riskKey: String, init: PredictionRiskModel.() -> Unit) =
        addElement(riskKey, init)

    override fun produceNew(key: String) = PredictionRiskModel(key)
    override fun mergeEl(inEl: PredictionRiskModel, fromEl: PredictionRiskModel) = inEl.merge(fromEl)
}

class PredictionRiskModel(
    override val key: String
) : ContainerPredictionModel<String, String, PredictionRiskCauseModel>() {
    @RiskCauseauseMarkerTag
    fun cause(riskCauseKey: String) = addElement(riskCauseKey) {}
    override fun produceNew(key: String) = PredictionRiskCauseModel(key)
    override fun mergeEl(inEl: PredictionRiskCauseModel, fromEl: PredictionRiskCauseModel) {

    }
}

class PredictionRiskCauseModel(override val key: String) : KeyProvider<String>

class PredictionModel : ContainerPredictionModel<ProcessKey, Any, PredictionProcessModel>() {
    override val key = Any()
    private val anyProcess = PredictionProcessModel(ProcessKey.AnyProcess)

    @ProcessMarkerTag
    fun anyProcess(init: PredictionProcessModel.() -> Unit) =
        anyProcess.apply(init)

    fun injectAnyProcess() = data.forEach { it.merge(anyProcess) }

    @ProcessMarkerTag
    fun process(processKey: ProcessKey, init: PredictionProcessModel.() -> Unit) =
        addElement(processKey, init)

    override fun produceNew(key: ProcessKey) = PredictionProcessModel(key)
    override fun mergeEl(inEl: PredictionProcessModel, fromEl: PredictionProcessModel) = inEl.merge(fromEl)

    companion object {
        fun buildModel(init: PredictionModel.() -> Unit) = PredictionModel().apply {
            init()
            injectAnyProcess()
        }
    }
}

abstract class ContainerPredictionModel<outK, ink, T : KeyProvider<outK>> : KeyProvider<ink> {
    private val mutableData = mutableListOf<T>()
    val data: List<T>
        get() = mutableData

    abstract fun produceNew(key: outK): T

    fun addElement(key: outK, init: T.() -> Unit) {
        if (mutableData.any { it.key == key })
            throw RuntimeException("Duplicate key $key")
        else mutableData.add(produceNew(key).apply(init))
    }

    fun merge(containerPredictionModel: ContainerPredictionModel<outK, ink, T>) {
        containerPredictionModel.data.forEach { el1 ->
            val findEl = data.find { it.key == el1.key }
            if (findEl != null) mergeEl(findEl, el1) else mutableData.add(el1)
        }
    }

    abstract fun mergeEl(inEl: T, fromEl: T)
}

interface KeyProvider<K> {
    val key: K
}
