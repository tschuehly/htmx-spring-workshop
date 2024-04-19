@param de.tschuehly.easy.spring.auth.htmx.HtmxComponent.HtmxContext htmxContext


<script src="https://unpkg.com/htmx.org@1.9.12"></script>

<form ${htmxContext.server().createUserEndpoint.call()}>
    <input type="text" name="userName">
    <input type="text" name="password">
    <button type="submit">
        Save
    </button>
</form>

@if(htmxContext.userForm() != null)
    <div>
        Hello ${htmxContext.userForm().userName()}! Your password is: ${htmxContext.userForm().password()}
    </div>
@endif