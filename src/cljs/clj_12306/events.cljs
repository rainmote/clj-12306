(ns clj-12306.events
  (:require [re-frame.core :as rf]
            [clj-12306.ajax]))

;;dispatchers

(def ip-regex #"(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)")

(rf/reg-event-db
  :initdb
  (fn [_ _]
    {:ip-regex ip-regex}))

(rf/reg-event-db
  :navigate
  (fn [db [_ & path]]
    (assoc db :nav-path path)))

(rf/reg-event-db
  :set-cdn-origin-data
  (fn [db [_ data]]
    (assoc db :cdn-origin-data data)))

(rf/reg-event-db
  :set-cdn-import-table
  (fn [db [_ data]]
    (assoc db :cdn-import-table data)))

(rf/reg-event-db
  :set-ip-regex
  (fn [db [_ data]]
    (assoc db :ip-regex data)))

(rf/reg-event-db
  :set-cdn-import-form
  (fn [db [_ data]]
    (assoc db :cdn-import-form data)))

(rf/reg-event-db
  :request-query-ip-info-success
  (fn [db [_ result]]
    (.log js/console result)))

(rf/reg-event-db
  :request-query-ip-info-failure
  (fn [db [_ result]]
    (.log js/console result)
    (assoc db :cdn-import-error result)))

(def graphql-query-ip-info
    "query {
        ipinfo(iplist: $iplist) {
            query
            city
            lat
            lon
            org
            country
            countryCode
    }}")

(rf/reg-event-fx
  :request-query-ip-info
  (fn [{:keys [db]} [_ data]]
    (.log js/console "query event.." data)
    {:http {:method :post
            :url "/api/graphql"
            :params {:query graphql-query-ip-info
                     :operationName "request-query-ip-info"
                     :variables {:iplist ["1.1.1.1" "114.114.114.114"]}}

            :success-event [:request-query-ip-info-success]
            :error-event [:request-query-ip-info-failure]}}))



;;subscriptions

(rf/reg-sub
  :nav-path
  (fn [db _]
    (:nav-path db)))

(rf/reg-sub
  :cdn-origin-data
  (fn [db _]
    (:cdn-origin-data db)))

(rf/reg-sub
  :ip-regex
  (fn [db _]
    (:ip-regex db)))

(rf/reg-sub
  :cdn-import-form
  (fn [db _]
    (:cdn-import-form db)))

(rf/reg-sub
  :cdn-import-table
  (fn [db _]
    (:cdn-import-table db)))

(rf/reg-sub
  :cdn-import-error
  (fn [db _]
    (:cdn-import-error db)))
