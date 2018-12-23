(ns clj-12306.http.client
  (:require [clj-http.client :as hc]
            [clj-12306.http.useragent :refer [user-agent-list]]
            [cheshire.core :refer :all]))

(def ^:dynamic *user-agent* (atom {}))

(defn query-user-agent [id]
  (-> @*user-agent*
      (get , id)
      (or ,
          (swap! *user-agent* assoc id (rand-nth user-agent-list)))))

(defn get-user-agent [id]
  (-> id
      (nil? ,)
      (if , (rand-nth user-agent-list)
            (query-user-agent id))))

(defn construct-request [{:keys [headers params]}]
  (-> {:content-type :json
       :accept :json
       :as :json
       :socket-timeout 3000
       :conn-timeout 3000
       :headers headers}
      (merge , params)))

(defn gen-uid []
  (-> (java.util.UUID/randomUUID)
      (str ,)
      (clojure.string/split , #"-")
      (first ,)))

(defn construct-headers
  ([] (construct-headers nil))
  ([user-agent-id]
   (-> {}
       (assoc , "User-Agent" (get-user-agent user-agent-id)))))

(defn batch-query-ip-info [params]
  (->> {:headers (construct-headers)
        :params {:form-params params}}
       (construct-request ,)
       (hc/post "http://ip-api.com/batch" ,)
       (:body ,)))
