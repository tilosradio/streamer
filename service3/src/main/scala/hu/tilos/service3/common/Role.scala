package hu.tilos.service3.common

object Role extends Enumeration {
  type Role = Value
  val Unknown, Guest, User, Author, Admin = Value
}

