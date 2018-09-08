(ns clj-12306.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[clj-12306 started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[clj-12306 has shut down successfully]=-"))
   :middleware identity})
