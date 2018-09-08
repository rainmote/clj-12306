(ns clj-12306.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [clj-12306.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[clj-12306 started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[clj-12306 has shut down successfully]=-"))
   :middleware wrap-dev})
