rootProject.name = "Coppin"
include("command")
include("command:fakes")
findProject(":command:fakes")?.name = "fakes"
include("models")
include("assignment")
include("utils")
include("db")
include("models")
include("sources")
include("utils:routing")
findProject(":utils:routing")?.name = "routing"
include("assignment:presenters")
findProject(":assignment:presenters")?.name = "presenters"
include("assignment:backend")
findProject(":assignment:backend")?.name = "backend"
include("assignment:api")
findProject(":assignment:api")?.name = "api"
include("utils")
include("assignment:backend:fakes")
findProject(":assignment:backend:fakes")?.name = "fakes"
include("sources:fakes")
findProject(":sources:fakes")?.name = "fakes"
include("utils:diff")
findProject(":utils:diff")?.name = "diff"
include("utils:files")
findProject(":utils:files")?.name = "files"
include("utils:diff:fakes")
findProject(":utils:diff:fakes")?.name = "fakes"
include("languages")
include("languages:api")
findProject(":languages:api")?.name = "api"
include("languages:backend")
findProject(":languages:backend")?.name = "backend"
include("languages:presenters")
findProject(":languages:presenters")?.name = "presenters"
include("users")
include("users:api")
findProject(":users:api")?.name = "api"
include("users:backend")
findProject(":users:backend")?.name = "backend"
include("users:presenters")
findProject(":users:presenters")?.name = "presenters"
