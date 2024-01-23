rootProject.name = "Coppin"
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
include("utils:files")
findProject(":utils:files")?.name = "files"
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
include("utils:authenticator")
findProject(":utils:authenticator")?.name = "authenticator"
include("utils:exposed")
findProject(":utils:exposed")?.name = "exposed"
include("dashboard")
include("dashboard:api")
findProject(":dashboard:api")?.name = "api"
include("dashboard:backend")
findProject(":dashboard:backend")?.name = "backend"
include("dashboard:presenters")
findProject(":dashboard:presenters")?.name = "presenters"
include("utils:logs")
findProject(":utils:logs")?.name = "logs"
include("utils:serialization")
findProject(":utils:serialization")?.name = "serialization"
include("courses")
include("courses:api")
findProject(":courses:api")?.name = "api"
include("courses:backend")
findProject(":courses:backend")?.name = "backend"
include("courses:presenters")
findProject(":courses:presenters")?.name = "presenters"
include("admin")
include("admin:backend")
findProject(":admin:backend")?.name = "backend"
include("admin:api")
findProject(":admin:api")?.name = "api"
include("admin:presenters")
findProject(":admin:presenters")?.name = "presenters"
