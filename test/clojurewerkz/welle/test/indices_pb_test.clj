(ns clojurewerkz.welle.test.indices-pb-test
  (:require [clojurewerkz.welle.core    :as wc]
            [clojurewerkz.welle.buckets :as wb]
            [clojurewerkz.welle.kv      :as kv]
            [clojure.test :refer :all]
            [clojurewerkz.welle.testkit :refer [drain]]
            [clojure.set :refer [subset?]])
  (:import java.util.UUID
           com.basho.riak.client.http.util.Constants))

(wc/connect-via-pb!)

(deftest ^{:2i true :edge-features true} test-basic-index-query-with-a-single-string-value-over-pb
  (let [bucket-name "clojurewerkz.welle.test.indices-pb-test"
        bucket      (wb/update bucket-name)
        k           (str (UUID/randomUUID))
        v           (.getBytes "value")
        indexes     {:email #{"johndoe@example.com" "timsmith@example.com"}}
        stored      (kv/store bucket-name k v :indexes indexes :content-type Constants/CTYPE_OCTET_STREAM)
        [idx-key]   (kv/index-query bucket-name :email "johndoe@example.com")
        [fetched]   (kv/fetch bucket-name idx-key)]
    (is (:indexes fetched))
    (is (= (String. ^bytes (:value fetched))
           (String. ^bytes v)))
    (is (= (:indexes fetched) indexes))
    (kv/delete bucket-name k)))
