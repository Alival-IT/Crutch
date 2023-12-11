const val PUBLISHING_GROUP = "sk.alival.crutch"

object AppCoordinates {
    const val APP_ID = "sk.alival.crutchTestApp"
    const val APP_VERSION_NAME = "1.0.0"
    const val APP_VERSION_CODE = 1
}

object CrutchConfigCommon {
    const val CRUTCH_GROUP_ID = "sk.alival.crutch"
}

object CrutchConfigCore {
    const val CRUTCH_LIB_ID = CrutchConfigCommon.CRUTCH_GROUP_ID + "." + "crutch_core"
}

object CrutchConfigStates {
    const val CRUTCH_LIB_ID = CrutchConfigCommon.CRUTCH_GROUP_ID + "." + "crutch_states"
}

object CrutchConfigCacheable {
    const val CRUTCH_LIB_ID = CrutchConfigCommon.CRUTCH_GROUP_ID + "." + "crutch_cacheable"
}