from .command_processor import CommandProcessor

_command_processor_instance = None

def get_command_processor():
    global _command_processor_instance
    if _command_processor_instance is None:
        _command_processor_instance = CommandProcessor()
    return _command_processor_instance