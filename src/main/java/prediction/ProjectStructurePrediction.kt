package main.java.prediction

import main.java.prediction.PredictionModel.Companion.buildModel


class ProjectStructurePrediction {

    val fullModel =
        buildModel {
            anyProcess {
                risk(VIOLATION_PROJECT_TIME_PLAN) {
                    cause(IMPORTANT_WORKER_ILL)
                    cause(EMERGENCY)
                    cause(VIOLATION_OF_PROCESS_PLAN)
                    cause(CONFLICTS_IN_TEAM)
                }
            }

            process(ProcessKey.Planing) {
                risk(VIOLATION_PROJECT_TIME_PLAN) {
                    cause(INVALID_TASKS_ESTIMATION)
                    cause(INVALID_TASK_EXECUTION_ORDER)
                    cause(INVALID_TASK_EXECUTORS)
                    cause(BAD_TECHNICAL_EQUIPMENT)
                    cause(NON_STABLE_TECHNICAL_TASK)
                    cause(LACK_TRAIN_SYSTEM)
                    cause(BAD_COMMUNICATION_WITH_CUSTOMER)
                }
                risk(BAD_USER_EXPERIENCE) {
                    cause(IGNORING_USER_REVIEWS)
                    cause(BAD_MARKETING_STRATEGY)
                    cause(NON_CONSISTENT_FUNCTIONAL_OF_DIFFERENT_PLATFORMS)
                    cause(BAD_COMMUNICATION_WITH_CUSTOMER)
                }
                risk(MISMATCH_MARKET_CONDITIONS){
                    cause(BAD_MARKET_ANALYZE)
                    cause(LACK_BUSINESS_EXPERT)
                    cause(BAD_UPDATE_POLITICS)
                }
                risk(VIOLATION_RIGHT_HOLDER) {
                    cause(NO_RIGHT_HOLDERS_DIALOG)
                    cause(INVALID_RIGHT_HOLDER_ANALYZE)
                    cause(BAD_COMMUNICATION_WITH_CUSTOMER)
                }
            }

            process(ProcessKey.Design) {
                risk(VIOLATION_PROJECT_TIME_PLAN) {
                    cause(ARCHITECTURE_OVERHEAD)
                    cause(REDESIGN_SYSTEM)
                    cause(BAD_ARCHITECTURE_SCALING)
                    cause(INVALID_DEVELOPER_LEVEL)
                }
                risk(PRODUCT_WORK_FAILURES) {
                    cause(WRONG_ARCHITECTURE)
                }
                risk(BAD_USER_EXPERIENCE) {
                    cause(WRONG_ARCHITECTURE)
                }
            }

            process(ProcessKey.Developing) {
                risk(VIOLATION_PROJECT_TIME_PLAN) {
                    cause(TERMINATION_OF_LIBRARY_SUPPORT)
                    cause(BAD_DEVELOPER_PERFORMANCE)
                    cause(BIG_COUNT_BUGS_ON_TEST)
                    cause(BAD_TEAMS_COMMUNICATION)
                    cause(BAD_ALGORITHM_COMPLEXITY)
                    cause(BAD_DOCUMENTATION_DEVELOPING)
                }

                risk(VIOLATION_RIGHT_HOLDER) {
                    cause(AUTHOR_CODE_USAGE)
                    cause(AUTHOR_MEDIA_USAGE)
                    cause(AUTOR_LIBRARIES_USAGE)
                }

                risk(PRODUCT_WORK_FAILURES) {
                    cause(NON_STABLE_ALGORITHMS)
                    cause(SITUATION_CODE)
                    cause(BAD_HIGH_LOAD_REALISATION)
                    cause(BAD_MULTI_THREADING)
                }

                risk(BAD_USER_EXPERIENCE) {
                    cause(BAD_TECHNICAL_REALIZATION)
                }
                risk(MISMATCH_MARKET_CONDITIONS) {
                    cause(BAD_ALGORITHM_PERFORMANCE)
                    cause(BAD_TECHNICAL_DEBT)
                    cause(BAD_ALGORITHMS_LIBRARIES)
                }
            }

            process(ProcessKey.UiUx) {
                risk(VIOLATION_PROJECT_TIME_PLAN) {
                    cause(INVALID_STANDARDS_USAGE)
                    cause(NOT_FULLY_ELABORATE_DESIGN)
                }
                risk(VIOLATION_RIGHT_HOLDER) {
                    cause(AUTHOR_MEDIA_USAGE)
                }
                risk(BAD_USER_EXPERIENCE) {
                    cause(HARD_UX_NO_HELP)
                }
                risk(MISMATCH_MARKET_CONDITIONS) {
                    cause(INVALID_STANDARD_USAGE)
                }
            }

            process(ProcessKey.Testing) {
                risk(VIOLATION_PROJECT_TIME_PLAN) {
                    cause(BIG_FUNCTIONAL_CONNECTIVITY)
                    cause(BAD_DOCUMENTATION_TESTING)
                }
                risk(PRODUCT_WORK_FAILURES) {
                    cause(BIG_COUNT_BUGS_LOSS_ON_TEST)
                    cause(BAD_TESTING_ENVIRONMENT)
                }
                risk(BAD_USER_EXPERIENCE) {
                    cause(BAD_FOUND_BUGS_TIME)
                    cause(UNEXPECTED_SYSTEM_STATE)
                }
                risk(MISMATCH_MARKET_CONDITIONS) {
                    cause(IGNORE_BAD_PERFORMANCE)
                    cause(BAD_TEST_CASE)
                }
            }

            process(ProcessKey.Delivery) {
                risk(VIOLATION_PROJECT_TIME_PLAN) {

                }
                risk(PRODUCT_WORK_FAILURES) {
                    cause(BAD_USER_ENVIRONMENT)
                }
                risk(BAD_USER_EXPERIENCE) {
                    cause(NOT_FULLY_ENVIRONMENT)
                    cause(USER_ENV_ACCESS)
                }
            }

        }

}

