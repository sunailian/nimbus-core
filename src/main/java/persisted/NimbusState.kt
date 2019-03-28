package persisted

data class NimbusState(
        val projectName: String,
        val compilationTimeStamp: String,
        val afterDeployments: MutableMap<String, MutableList<String>> = mutableMapOf(),
        //Stage -> Bucket -> LocalFile -> RemoteFile
        val fileUploads: MutableMap<String, MutableMap<String, MutableMap<String, String>>> = mutableMapOf()
)