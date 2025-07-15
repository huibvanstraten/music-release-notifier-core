rootProject.name = "music-release-notifier-core"
include(":outbox", ":music-release-notifier-core")
project(":outbox").projectDir = file("outbox")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
