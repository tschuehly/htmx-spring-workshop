@import static de.tschuehly.easy.spring.auth.user.UserController.POST_CREATE_USER
<div style="width: 100dvw; height: 100dvh; position: fixed; top: 0;left: 0; background-color: rgba(128,128,128,0.69); display: flex; justify-content: center; align-items: center;">
    <form style="background-color: whitesmoke; padding: 2rem;"
          hx-post="${POST_CREATE_USER}"> <%-- (1) --%>
        <label>
            Username
            <input type="text" name="username">
        </label>
        <label>
            Password
            <input type="text" name="password">
        </label>
        <button type="submit"> <%-- (2) --%>
            Save User
        </button>
    </form>
</div>