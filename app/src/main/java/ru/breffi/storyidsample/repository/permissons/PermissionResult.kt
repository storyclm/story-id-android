package ru.breffi.storyidsample.repository.permissons

sealed class PermissionResult {

    class Granted(val grantedPermissions: Collection<String>) : PermissionResult()

    open class Denied(val deniedPermissions: Collection<String>) : PermissionResult()

    class NeedsRationale(deniedPermissions: Collection<String>) : Denied(deniedPermissions)
    class DoNotAskAgain(deniedPermissions: Collection<String>) : Denied(deniedPermissions)
}