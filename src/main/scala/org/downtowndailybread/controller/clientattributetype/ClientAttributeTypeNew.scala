package org.downtowndailybread.controller.clientattributetype

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import org.downtowndailybread.json.JsonSupport
import org.downtowndailybread.model.ClientAttributeType
import org.downtowndailybread.request.{ClientAttributeTypeRequest, DatabaseSource}

class ClientAttributeTypeNew extends JsonSupport {

  def newClientAttributeTypeRoute() = {
    path("new") {
      post {
        entity(as[Seq[ClientAttributeType]]) {
          cats =>
            val newAttribIds = cats.map { cat =>
              DatabaseSource.runSql(c =>
                new ClientAttributeTypeRequest(c).insertClientAttributeType(cat))
            }
            complete(StatusCodes.Created)
        }
      }
    }
  }
}