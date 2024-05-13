@import de.tschuehly.easy.spring.auth.web.message.MessageComponent.MessageContext
@import java.util.Date
@param MessageContext messageContext

!{var id = String.valueOf(new Date().getTime());}
<div role="alert" id="${id}" style="position: fixed; margin: 2rem; top: 0; left: 0; border-radius: 1rem; background-color: antiquewhite; padding: 1rem;">
    <button onclick="document.getElementById('${id}').style.display = 'none'">
        <i class="">X</i>
    </button>
    <div>
        <h2>${messageContext.type().severity()}: ${messageContext.message()}</h2>
    </div>
</div>
