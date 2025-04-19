from rest_framework_simplejwt.authentication import JWTAuthentication
from rest_framework_simplejwt.exceptions import AuthenticationFailed
from api.models import BlacklistedToken
from django.core.cache import cache
from django.utils import timezone

class CustomJWTAuthentication(JWTAuthentication):
    def get_validated_token(self, raw_token):
        self.cleanup_expired_blacklisted_tokens()
        token = super().get_validated_token(raw_token)
        jti = token.get("jti")

        if BlacklistedToken.objects.filter(jti=jti).exists():
            raise AuthenticationFailed("Token has been blacklisted")

        return token
    
    def cleanup_expired_blacklisted_tokens(self):
        if not cache.get('recent_token_cleanup'):
            count = BlacklistedToken.objects.filter(exp__lt=timezone.now()).delete()[0]
            if count:
                print(f"🧹 Dọn dẹp {count} token hết hạn.")
            cache.set('recent_token_cleanup', True, timeout=300)