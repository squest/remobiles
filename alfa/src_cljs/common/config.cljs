(ns common.config)

(def config {:production {:database "app-database-production"}
						 :development {:database "app-database-development"}
						 :test {:database "app-database-test"}
						 :test-1 {:database "app-database-test-1"}
						 :available-drills ["one" "two" "three"]})
