(ns user
  (:require [clj-12306.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [clj-12306.figwheel :refer [start-fw stop-fw cljs]]
            [clj-12306.core :refer [start-app]]
            [clj-12306.db.core]
            [conman.core :as conman]
            [luminus-migrations.core :as migrations]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start []
  (mount/start-without #'clj-12306.core/repl-server))

(defn stop []
  (mount/stop-except #'clj-12306.core/repl-server))

(defn restart []
  (stop)
  (start))

(defn restart-db []
  (mount/stop #'clj-12306.db.core/*db*)
  (mount/start #'clj-12306.db.core/*db*)
  (binding [*ns* 'clj-12306.db.core]
    (conman/bind-connection clj-12306.db.core/*db* "sql/queries.sql")))

(defn reset-db []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration [name]
  (migrations/create name (select-keys env [:database-url])))


