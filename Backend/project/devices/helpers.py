def convert_to_group(home_email, type="esp32"):
    home_group = home_email.replace('@', '_at_').replace('.', '_dot_')
    return f"{type}_{home_group}"