import axios, { AxiosError, AxiosResponse } from "axios";
import { useEffect, useState } from "react";
import { Alert, Button, Container, FloatingLabel, Form, InputGroup } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import { useBackgroundImage } from "../../CustomHooks/useBackgroundImage";

export const Login: React.FC = () => {

    const baseUrl = localStorage.getItem("baseUrl");

    useBackgroundImage();
    const navigate = useNavigate();

    const [user, setUser] = useState({
        username: "",
        password: ""
    });
    const [passwordIsVisible, setPasswordIsVisible] = useState(false);
    const [successfullyRegistered, setSuccessfullyRegistered] = useState(false);
    const [loginError, setLoginError] = useState(false);
    const [loginErrorMessage, setLoginErrorMessage] = useState("");

    useEffect(() => {
        const hasRegistered = localStorage.getItem("hasRegistered");
        if (hasRegistered) {
            setSuccessfullyRegistered(true);
            localStorage.removeItem("hasRegistered");
        }
    }, []);

    const storeValues = (input: React.ChangeEvent<HTMLInputElement>) => {
        if (input.target.name === "username") {
            setUser((user) => ({...user, username:input.target.value}));
        } else {
            setUser((user) => ({...user, password:input.target.value}));
        }
    };

    const togglePasswordVisibility: React.MouseEventHandler<HTMLButtonElement> = () => {
        setPasswordIsVisible(!passwordIsVisible);
    }

    const login = async () => {
        const resp = await axios.post(baseUrl + "/login", user, {withCredentials: true})
        .then((resp: AxiosResponse) => {
            localStorage.setItem("user", JSON.stringify(resp.data));
            navigate("/dashboard");
        })
        .catch((error: AxiosError) => {
            if (successfullyRegistered) {
                setSuccessfullyRegistered(false);
            }
            setLoginErrorMessage(`${error.response?.data}`);
            setLoginError(true);
        });
    };

    return (
        <Container className="d-flex flex-column justify-content-center m-5 px-5">
            <Container className="w-75 bg-light bg-opacity-50 rounded p-3">
                <header className="text-center">
                    <h1 className="fs-2">Employee Reimbursement System</h1>
                    <p className="fs-5">Sign in to view/manage your reimbursements</p>
                </header>
                <Form>
                    <Form.Group className="mb-3">
                        <FloatingLabel label="Username">
                            <Form.Control type="text" name="username" className="border border-2" onChange={storeValues} />
                        </FloatingLabel>
                    </Form.Group>
                    <Form.Group className="mb-3">
                        <InputGroup>
                            <FloatingLabel label="Password">
                                <Form.Control type={passwordIsVisible ? "text": "password"} name="password" className="border border-2" onChange={storeValues} aria-describedby="passwordVisibility" />
                            </FloatingLabel>
                            <Button onClick={togglePasswordVisibility} id="passwordVisibility">
                                {passwordIsVisible ? <i className="bi bi-eye fs-3"></i> : <i className="bi bi-eye-slash fs-3"></i>}
                            </Button>
                        </InputGroup>
                    </Form.Group>
                </Form>
                <div>
                    <Button type="button" variant="primary" className="mx-3" onClick={login}>Login</Button>
                    <Button type="button" variant="secondary" onClick={() => navigate("/register")}>Register</Button>
                </div>
                {successfullyRegistered && (
                    <Alert variant="success" className="mt-3" onClose={() => setSuccessfullyRegistered(false)} dismissible>
                        <Alert.Heading>Success!</Alert.Heading>
                        <p>You have successfully registered an account</p>
                    </Alert>
                )}
                {loginError && (
                    <Alert variant="danger" className="mt-3" onClose={() => setLoginError(false)} dismissible>
                        <Alert.Heading>Failed!</Alert.Heading>
                        <p>{loginErrorMessage}</p>
                    </Alert>
                )}
            </Container>
        </Container>
    )
}