(ns clj-12306.cdn.ip
  (:require [clj-http.client :as client]
            [clj-12306.http.useragent :as ua]
            [clojure.tools.logging :as log]))

(def url "http://ip-api.com/batch?lang=zh-CN")

(def default-headers
  {"Accept-Encoding" "gzip, deflate"})

(defn construct-headers [header]
  (-> default-headers
      (assoc , "User-Agent" (rand-nth ua/user-agent-list))
      (merge , (or header {}))))

(def http-proxy
  {:proxy-host "127.0.0.1"
   :proxy-port 8080})

(defn retry-fn [ex try-count http-context]
  (log/warn http-context)
  (log/warn ex)
  (-> (> try-count 3)
      (if , false true)))

(defn construct-params
  [{:keys [form-params headers]
    :or {form-params {}
         headers {}}
    :as param}]
  (-> (merge , param)
      ;;(merge , http-proxy)
      (assoc , :content-type :json)
      (assoc , :accept :json)
      (assoc , :as :json)
      (assoc , :throw-exceptions false)
      (assoc , :retry-handler retry-fn)
      (assoc , :form-params form-params)
      (assoc , :headers (construct-headers headers))))

(defn batch-query
  [{:keys [iplist fields]}]
  (->> iplist
       (map #(assoc {} :query %) ,)
       (map #(if fields
               (assoc % :fields fields)
               (identity %)) ,)
       (assoc {} :form-params ,)
       (construct-params ,)
       (client/post url ,)
       :body))

(defn query [iplist]
  (->> iplist
       (partition-all 100 ,)
       (map #(batch-query {:iplist %}) ,)
       (flatten ,)))

(comment
  (batch-query {:iplist ["114.114.114.114" "112.10.106.99"]})
  )
