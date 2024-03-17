@import static de.tschuehly.easy.spring.auth.controller.UserController.SAVE_USER
@import org.springframework.util.StringUtils
@param de.tschuehly.easy.spring.auth.controller.UserController.UserForm userForm

<div style="width: 100dvw; height: 100dvh; position: fixed; top: 0;left: 0; background-color: rgba(128,128,128,0.69); display: flex; justify-content: center; align-items: center;">
    <form style="background-color: whitesmoke; padding: 2rem;">
        @if(StringUtils.hasText(userForm.uuid()))
            <label>
                UUID
                <input type="text" readonly name="uuid" value="${userForm.uuid()}">
            </label>
        @endif
        <label>
            Username
            <input type="text" name="username" value="${userForm.username()}">
        </label>
        <label>
            Password
            <input type="text" name="password" value="${userForm.password()}">
        </label>
        <button type="submit" hx-post="${SAVE_USER}">
            Save User
        </button>
    </form>
</div>