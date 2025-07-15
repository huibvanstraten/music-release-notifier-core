rootProject.name = "music-release-notifier-core"
include(":outbox")
project(":outbox").projectDir = file("outbox")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
