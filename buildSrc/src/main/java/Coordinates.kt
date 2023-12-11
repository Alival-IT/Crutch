const val PUBLISHING_GROUP = "sk.alival.crutch"

object AppCoordinates {
    const val APP_ID = "sk.alival.crutchTestApp"
    const val APP_VERSION_NAME = "1.0.0"
    const val APP_VERSION_CODE = 1
}

object CrutchConfigCommon {
    const val CRUTCH_GROUP_ID = "sk.alival.crutch"
    const val CRUTCH_VERSION_NAME = "0.0.5-alpha5"
}

object CrutchConfigCore {
    const val CRUTCH_ARTIFACT_ID = "crutch"
    const val CRUTCH_GROUP_ID = CrutchConfigCommon.CRUTCH_GROUP_ID
    const val CRUTCH_LIB_ID = CrutchConfigCommon.CRUTCH_GROUP_ID + "." + CRUTCH_ARTIFACT_ID
    const val CRUTCH_VERSION_NAME = CrutchConfigCommon.CRUTCH_VERSION_NAME
}

object CrutchConfigStates {
    const val CRUTCH_ARTIFACT_ID = "crutch_states"
    const val CRUTCH_GROUP_ID = CrutchConfigCommon.CRUTCH_GROUP_ID
    const val CRUTCH_LIB_ID = CrutchConfigCommon.CRUTCH_GROUP_ID + "." + CRUTCH_ARTIFACT_ID
    const val CRUTCH_VERSION_NAME = CrutchConfigCommon.CRUTCH_VERSION_NAME
}