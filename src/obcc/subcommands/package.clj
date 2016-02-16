;; Licensed to the Apache Software Foundation (ASF) under one
;; or more contributor license agreements.  See the NOTICE file
;; distributed with this work for additional information
;; regarding copyright ownership.  The ASF licenses this file
;; to you under the Apache License, Version 2.0 (the
;; "License"); you may not use this file except in compliance
;; with the License.  You may obtain a copy of the License at
;;
;;   http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing,
;; software distributed under the License is distributed on an
;; "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
;; KIND, either express or implied.  See the License for the
;; specific language governing permissions and limitations
;; under the License.
(ns obcc.subcommands.package
  (:require [obcc.config.util :as config.util]
            [obcc.config.parser :as config]
            [obcc.dar.write :as dar]
            [obcc.dar.ls :refer :all]
            [clojure.java.io :as io]
            [clojure.tools.cli :refer [parse-opts]]))

(defn findfirst [spec config] (first (config/find [:configuration spec] config)))

(defn composename [config]
  (let [name (findfirst :name config)
        version (findfirst :version config)]
    (str name "-" version)))

(defn getoutputfile [options path config]
  (if-let [output (:output options)]
    (io/file output)
    (io/file path "build" (str (composename config) ".cca"))))

(defn run [options args]
  (let [[path config] (config.util/load-from-options options)
        filespec ["src" "chaincode.conf"]
        compressiontype (:compress options)
        outputfile (getoutputfile options path config)]

    ;; emit header information after we know the file write was successful
    (println "Writing CCA to:" (.getAbsolutePath outputfile))
    (println "Using path" path (str filespec))

    ;; generate the actual file
    (dar/write path filespec compressiontype outputfile)

    ;; re-use the ls function to display the contents
    (ls outputfile)))
