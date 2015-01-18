(ns common.config)

(def config {:production {:database "app-database-production"}
						 :development {:database "app-database-development"}
						 :test {:database "app-database-test"}
						 :available-drills ["one" "two" "three"]})
