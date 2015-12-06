package hu.tilos.service3

import hu.tilos.service3.common.Role._
import hu.tilos.service3.entities.AuthorObj

case class UserObj(id: String, username: String, role: Role, author: Option[AuthorObj]) {
  def addAuthor(newAuthor: Some[AuthorObj]): UserObj = {
    return UserObj(id, username, role, newAuthor)
  }

}