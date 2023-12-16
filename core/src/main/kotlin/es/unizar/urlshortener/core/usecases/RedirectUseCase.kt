@file:Suppress("WildcardImport")

package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.Redirection
import es.unizar.urlshortener.core.RedirectionNotFound
import es.unizar.urlshortener.core.ShortUrl
import es.unizar.urlshortener.core.ShortUrlRepositoryService
import es.unizar.urlshortener.core.ValidatorService
import es.unizar.urlshortener.core.RedirectionNotReachableException

/**
 * Given a key returns a [Redirection] that contains a [URI target][Redirection.target]
 * and an [HTTP redirection mode][Redirection.mode].
 *
 * **Note**: This is an example of functionality.
 */
interface RedirectUseCase {
    fun redirectTo(key: String): Redirect
}

data class Redirect(val value: Redirection, val interstitial: Boolean)
fun ShortUrl.toRedirect() = Redirect(value = redirection, interstitial = properties.interstitial ?: false)

class RedirectUseCaseImpl(
    private val shortUrlRepository: ShortUrlRepositoryService,
    private val validatorService: ValidatorService
) : RedirectUseCase {
    override fun redirectTo(key: String) : Redirect {
        val redirection = shortUrlRepository
        .findByKey(key)
        ?.toRedirect()
        ?: throw RedirectionNotFound(key)

        //verify if url in the db is reachable
        val url = redirection.value.target
        if (!validatorService.isReachable(url)) {
            throw RedirectionNotReachableException(url)
        }   

        return redirection
    }
             
}

