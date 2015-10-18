package hu.tilos.service3.entities

import hu.tilos.service3.common.Role._

case class UserObj(id: String, username: String, role: Role, author: AuthorObj, shows: List[ShowObj], permissions: List[String]) {

}