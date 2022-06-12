package org.oneofthezombies.utility

import groovy.json.JsonSlurper

class ParameterUtil {

    final Script script
    static final String PARAMETER_API = 'http://localhost:3000'

    ParameterUtil(Script script) {
        this.script = script
    }

    Map createAPIChoiceParam(Map args) {
        String path = args.path
        String name = args.name
        String description = args.description
        return [
            $class: 'ChoiceParameter',
            choiceType: 'PT_SINGLE_SELECT',
            description: description,
            filterLength: 1,
            filterable: false,
            name: name,
            script: [
                $class: 'GroovyScript',
                fallbackScript: [
                    classpath: [],
                    sandbox: false,
                    script:
                        """return ['ERROR']"""
                ],
                script: [
                    classpath: [],
                    sandbox: false,
                    script: """import groovy.json.JsonSlurper

HttpURLConnection connection = null
try {
    connection = new URL("${this.PARAMETER_API}/${path}").openConnection()
    connection.requestMethod = 'GET'
    connection.connectTimeout = 1
    connection.readTimeout = 3
    connection.connect()
    if (connection.responseCode != 200) {
        throw new Exception(connection.toString())
    }
    return new JsonSlurper().parseText(connection.inputStream.text)
} finally {
    try {
        if (connection != null) {
            connection.disconnect()
        }
    } catch (Exception e) {
        // do nothing
    }
}
"""
                ]
            ]
        ]
    }

}
