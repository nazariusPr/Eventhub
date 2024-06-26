import React, { useState, useEffect, useContext } from "react";
import axios from "../../../../api/axios";
import styles from "./SignIn.module.css";
import { Link, Navigate, Redirect, useAsyncError } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import { LockOutlined, UserOutlined } from "@ant-design/icons";
import { Button, Checkbox, Form, Input, message } from "antd";
import CloseWindowButton from "../../../../components/Buttons/CloseWindowButton/CloseWindowButton";
import { GoogleLogin } from "@react-oauth/google";
import { checkEmail } from "../../SignUp/Registration/validation";
import AuthContext from "../../../../context/authProvider";
import EmailVerification from "../../SignUp/EmailVerification/EmailVerification";

const SignIn = ({ forgotPassword }) => {
  const { login, googleAuth } = useContext(AuthContext);

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [navigate, setNavigate] = useState(false);
  const [isVerified, setIsVerified] = useState(true);
  const navigateToHome = useNavigate();

  const successGoogleLogin = async (credentialResponse) => {
    const googleToken = credentialResponse.credential;

    const res = await googleAuth(googleToken);

    const email = res?.data?.email;
    const accessToken = res?.data?.accessToken;
    setEmail(email);
    if (!accessToken) {
      setIsVerified(false);
    } else {
      navigateToHome("/");
      window.location.reload();
      message.success("Login successful!");
    }
  };

  const onFinish = async () => {
    try {
      await login(email, password);

      message.success("Login successful!");
      navigateToHome("/");
      window.location.reload();
      //setNavigate(true);
    } catch (err) {
      if (!err.response) {
        // Помилка з'єднання з сервером
        message.error("No server response");
      } else {
        const status = err.response.status;
        switch (status) {
          case 400:
            // Помилка валідації даних на сервері
            message.error(
              "Invalid email or password. Please check your input."
            );
            break;
          case 401:
            // Користувач не авторизований
            message.error("Unauthorized: Please check your credentials.");
            break;
          case 403:
            // Доступ заборонено
            message.error(
              "Forbidden: You do not have permission to access this resource."
            );
            break;
          case 404:
            // URL не знайдено
            message.error("Not Found: The requested resource was not found.");
            break;
          case 409:
            // Конфлікт
            message.error("Conflict: The resource already exists.");
            break;
          case 422:
            // Невірні вхідні дані
            message.error(
              "Unprocessable Entity: The request was well-formed but unable to be followed due to semantic errors."
            );
            break;
          case 500:
            // Внутрішня помилка сервера
            message.error(
              "Internal Server Error: Something went wrong on the server."
            );
            break;
          default:
            // Інші типи помилок
            console.error(err);
            message.error("Registration failed: " + err.response.data);
            break;
        }
      }
    }
  };
  const handleKeyDown = (event) => {
    if (event.key === "Escape") {
      navigateToHome("/");
    }
  };
  useEffect(() => {
    document.addEventListener("keydown", handleKeyDown);
    return () => {
      document.removeEventListener("keydown", handleKeyDown);
    };
  }, []);
  if (navigate) {
    return <Navigate to="/" />;
  }

  return isVerified ? (
    <div className={styles.container}>
      <div className={styles.Buttons}>
        <CloseWindowButton onClick={() => navigateToHome("/")} />
      </div>
      <Form
        name="normal_login"
        className="login-form"
        initialValues={{
          remember: true,
        }}
        onFinish={onFinish}
      >
        <h1>Login</h1>
        <Form.Item
          name="email"
          rules={[
            {
              required: true,
              validator: checkEmail,
              message: "Please input your email!",
            },
          ]}
        >
          <Input
            prefix={<UserOutlined className="site-form-item-icon" />}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Email"
            className={styles.input}
          />
        </Form.Item>
        <Form.Item
          name="password"
          rules={[
            {
              required: true,
              message: "Please input your Password!",
            },
          ]}
        >
          <Input.Password
            className={styles.input}
            prefix={<LockOutlined className="site-form-item-icon" />}
            onChange={(e) => setPassword(e.target.value)}
            type="password"
            placeholder="Password"
          />
        </Form.Item>
        <Form.Item className={styles.forgotPasswordContainer}>
          <div className={styles.RememberMe}>
            {/* <Form.Item name="remember" valuePropName="checked" noStyle>
              <Checkbox>Remember me</Checkbox>
            </Form.Item> */}
            <button className={styles.forgotPassword} onClick={forgotPassword}>
              Forgot password
            </button>
          </div>
        </Form.Item>
        <Form.Item style={{ marginBottom: "0px" }}>
          <Button
            type="primary"
            htmlType="submit"
            className={styles.loginButton}
          >
            Login
          </Button>
        </Form.Item>
        <p style={{ textAlign: "center" }}>Or</p>
        <div className={styles.oauthContainer}>
          <GoogleLogin
            onSuccess={successGoogleLogin}
            onError={() => {
              console.log("Login Failed");
            }}
            useOneTap
            ux_mode="popup"
            shape="pill"
            // login_uri="http://localhost:3000/login"
          />
        </div>
        <p
          style={{
            textAlign: "center",
            fontSize: "12px",
            margin: "21px",
          }}
        >
          Don’t have an account in EventHub yet?{" "}
          <Link to="/register">Register!</Link>
        </p>
      </Form>
    </div>
  ) : (
    <EmailVerification email={email} />
  );
};
export default SignIn;
