package graphql

import spock.lang.Specification

class RelaySchemaTest extends Specification {

    def "Validate Relay Node schema"() {

        given:
        def query = """{
                      __schema {
                        queryType {
                          fields {
                            name
                            type {
                              name
                              kind
                            }
                            args {
                              name
                              type {
                                kind
                                ofType {
                                  name
                                  kind
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                    """
        when:
        def data = GraphQL.newGraphQL(RelaySchemaKt.Schema).build().execute(query).toCompletableFuture().get().data()

        then:
        def nodeField = data["__schema"]["queryType"]["fields"][0];
        nodeField == [name: "node", type: [name: "Node", kind: "INTERFACE"], args: [[name: "id", type: [kind: "NON_NULL", ofType: [name: "ID", kind: "SCALAR"]]]]]
    }


    def "Validate Relay StuffConnection schema"() {

        given:
        def query = """{
                          __type(name: "StuffConnection") {
                            fields {
                              name
                              type {
                                name
                                kind
                                ofType {
                                  name
                                  kind
                                }
                              }
                            }
                          }
                        }"""
        when:
        def data = GraphQL.newGraphQL(RelaySchemaKt.Schema).build().execute(query).toCompletableFuture().get().data()

        then:
        def fields = data["__type"]["fields"];
        fields == [[name: "edges", type: [name: null, kind: "LIST", ofType: [name: "StuffEdge", kind: "OBJECT"]]], [name: "pageInfo", type: [name: null, kind: "NON_NULL", ofType: [name: "PageInfo", kind: "OBJECT"]]]]
    }

    def "Validate Relay StuffEdge schema"() {

        given:
        def query = """{
                          __type(name: "StuffEdge") {
                            fields {
                              name
                              type {
                                name
                                kind
                                ofType {
                                  name
                                  kind
                                }
                              }
                            }
                          }
                        }
                    """
        when:
        def data = GraphQL.newGraphQL(RelaySchemaKt.Schema).build().execute(query).toCompletableFuture().get().data()

        then:
        def fields = data["__type"]["fields"];
        fields == [[name: "node", type: [name: "Stuff", kind: "OBJECT", ofType: null]], [name: "cursor", type: [name: null, kind: "NON_NULL", ofType: [name: "String", kind: "SCALAR"]]]]
    }

}
