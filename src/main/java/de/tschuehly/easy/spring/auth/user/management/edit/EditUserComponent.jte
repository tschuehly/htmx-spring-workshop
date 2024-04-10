@import static de.tschuehly.easy.spring.auth.user.management.edit.EditUserComponent.*
@import de.tschuehly.easy.spring.auth.htmx.CreateUserEndpoint
@import de.tschuehly.easy.spring.auth.htmx.HtmxAction
@import de.tschuehly.easy.spring.auth.htmx.HtmxController
@import de.tschuehly.easy.spring.auth.htmx.HtmxController.UserForm
@import de.tschuehly.easy.spring.auth.user.UserController
@import de.tschuehly.easy.spring.auth.user.management.edit.EditUserComponent
@param EditUserComponent.EditUserContext editUserContext

<div style="width: 100dvw; height: 100dvh; position: fixed; top: 0;left: 0; background-color: rgba(128,128,128,0.69); display: flex; justify-content: center; align-items: center;">
    <form style="background-color: whitesmoke; padding: 2rem;">
        <label>
            UUID
            <input type="text" readonly name="uuid" value="${editUserContext.uuid().toString()}">
        </label>
        <label>
            Username
            <input type="text" name="userName" value="${editUserContext.username()}">
        </label>
        <label>
            Password
            <input type="text" name="password" value="${editUserContext.password()}">
        </label>
        <button type="submit" ${HtmxController.createUserEndpoint}>
            Save User
        </button>
    </form>
</div>