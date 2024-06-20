import ast
import sys


class MethodInfoExtractor(ast.NodeVisitor):
    def __init__(self):
        self.methods = []

    def visit_FunctionDef(self, node):
        method_info = {
            'name': node.name,
            'args': [(arg.arg, self.get_annotation(arg.annotation)) for arg in node.args.args],
            'return_type': self.get_annotation(node.returns)
        }
        self.methods.append(method_info)

    def get_annotation(self, annotation):
        if annotation is None:
            return None
        elif isinstance(annotation, ast.Name):
            return annotation.id
        elif isinstance(annotation, ast.Subscript):
            value = self.get_annotation(annotation.value)
            slice = self.get_annotation(annotation.slice)
            return f"{value}[{slice}]"
        elif isinstance(annotation, ast.Attribute):
            return f"{annotation.value.id}.{annotation.attr}"
        elif isinstance(annotation, ast.Index):  # For Python versions < 3.9
            return self.get_annotation(annotation.value)
        elif isinstance(annotation, ast.Constant):  # For Python 3.9 and above
            return annotation.value
        elif isinstance(annotation, ast.Tuple):
            return ', '.join(map(self.get_annotation, annotation.elts))
        return str(annotation)


def extract_method_info_from_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as file:
        tree = ast.parse(file.read(), filename=filepath)
    extractor = MethodInfoExtractor()
    extractor.visit(tree)
    return extractor.methods


if __name__ == "__main__":
    filepath = sys.argv[1]
    methods_info = extract_method_info_from_file(filepath)
    for method in methods_info:
        method_name = method['name']
        args = []
        for arg_name, arg_type in method['args']:
            if arg_name != 'self':
                args.append(arg_type)
        args_str = ",".join(args)

        print(f"name:{method_name} args:{args_str}")
