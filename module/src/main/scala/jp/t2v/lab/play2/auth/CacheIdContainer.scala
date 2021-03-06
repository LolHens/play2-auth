package jp.t2v.lab.play2.auth

import java.security.SecureRandom
import java.util.concurrent.TimeUnit

import play.api.cache.SyncCacheApi

import scala.annotation.tailrec
import scala.concurrent.duration.Duration
import scala.reflect.ClassTag
import scala.util.Random

class CacheIdContainer[Id: ClassTag](cacheApi: SyncCacheApi) extends IdContainer[Id] {

  private[auth] val tokenSuffix = ":token"
  private[auth] val userIdSuffix = ":userId"
  private[auth] val random = new Random(new SecureRandom())

  def startNewSession(userId: Id, timeoutInSeconds: Int): AuthenticityToken = {
    removeByUserId(userId)
    val token = generate
    store(token, userId, timeoutInSeconds)
    token
  }

  @tailrec
  private[auth] final def generate: AuthenticityToken = {
    val table = "abcdefghijklmnopqrstuvwxyz1234567890_.~*'()"
    val token = Iterator.continually(random.nextInt(table.size)).map(table).take(64).mkString
    if (get(token).isDefined) generate else token
  }

  private[auth] def removeByUserId(userId: Id): Unit = {
    cacheApi.get[String](userId.toString + userIdSuffix) foreach unsetToken
    unsetUserId(userId)
  }

  def remove(token: AuthenticityToken): Unit = {
    get(token).foreach(unsetUserId)
    unsetToken(token)
  }

  private[auth] def unsetToken(token: AuthenticityToken): Unit = {
    cacheApi.remove(token + tokenSuffix)
  }
  private[auth] def unsetUserId(userId: Id): Unit = {
    cacheApi.remove(userId.toString + userIdSuffix)
  }

  def get(token: AuthenticityToken) = cacheApi.get(token + tokenSuffix).map(_.asInstanceOf[Id])

  private[auth] def store(token: AuthenticityToken, userId: Id, timeoutInSeconds: Int): Unit = {
    def intToDuration(seconds: Int): Duration = if (seconds == 0) Duration.Inf else Duration(seconds, TimeUnit.SECONDS)
    cacheApi.set(token + tokenSuffix, userId, intToDuration(timeoutInSeconds))
    cacheApi.set(userId.toString + userIdSuffix, token, intToDuration(timeoutInSeconds))
  }

  def prolongTimeout(token: AuthenticityToken, timeoutInSeconds: Int): Unit = {
    get(token).foreach(store(token, _, timeoutInSeconds))
  }

}
